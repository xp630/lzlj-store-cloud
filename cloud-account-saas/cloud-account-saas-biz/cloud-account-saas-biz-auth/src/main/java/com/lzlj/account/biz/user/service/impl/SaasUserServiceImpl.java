package com.lzlj.account.biz.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.dev33.satoken.stp.StpUtil;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.AuthException;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.helper.RedisHelper;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.config.SaasSaTokenConfig;
import com.lzlj.account.permission.service.PermissionService;
import com.lzlj.account.biz.sms.service.SmsCodeService;
import com.lzlj.account.biz.systemparameter.dto.SystemParameterDTO;
import com.lzlj.account.biz.tenant.dto.AssignTenantDTO;
import com.lzlj.account.biz.tenant.service.AdminTenantService;
import com.lzlj.account.biz.systemparameter.service.SystemParameterService;
import com.lzlj.account.biz.user.dao.SaasUserDao;
import com.lzlj.account.biz.user.dto.CreateUserDTO;
import com.lzlj.account.biz.user.dto.UserLoginDTO;
import com.lzlj.account.biz.user.dto.UpdateUserDTO;
import com.lzlj.account.biz.user.dto.UserQueryDTO;
import com.lzlj.account.biz.user.entity.SaasUser;
import com.lzlj.account.biz.user.service.SaasUserRoleService;
import com.lzlj.account.biz.user.service.SaasUserService;
import com.lzlj.account.biz.user.dto.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaasUserServiceImpl implements SaasUserService {

    private final SaasUserDao userDao;
    private final RedisHelper redisHelper;
    private final SmsCodeService smsCodeService;
    private final SystemParameterService systemParameterService;
    private final PermissionService permissionService;
    private final SaasSaTokenConfig saTokenConfig;
    private final SaasUserRoleService userRoleService;
    private final AdminTenantService adminTenantService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    private static final String TOKEN_PREFIX = "token:";
    private static final String USER_INFO_PREFIX = "user:info:";
    private static final String SMS_LOGIN_WHITELIST_KEY = "sms_login_whitelist";
    private static final String BYPASS_CODE = "000000";

    @Override
    public String login(UserLoginDTO loginDTO) {
        SaasUser user;

        // 1. 根据 loginType 判断登录方式
        // loginType = 1 → 管理员：用户名 + 密码
        // loginType = 2 → 用户：手机号 + 密码 + 验证码
        if (loginDTO.getLoginType() != null && loginDTO.getLoginType() == 2) {
            // 普通用户：手机号 + 密码 + 短信验证码
            user = userDao.selectByPhoneWithoutTenant(loginDTO.getUsername());
            if (user == null) {
                throw new AuthException(ResultCode.ACCOUNT_DISABLED);
            }
            // 验证短信验证码
            verifySmsCode(user.getPhone(), loginDTO.getSmsCode());
        } else {
            // 管理员：用户名 + 密码
            user = userDao.selectByUsernameWithoutTenant(loginDTO.getUsername());
            if (user == null) {
                throw new AuthException(ResultCode.ACCOUNT_DISABLED);
            }
        }

        // 2. 验证密码
        String encryptPassword = encryptPassword(loginDTO.getPassword(), user.getSalt());
        if (!encryptPassword.equals(user.getPassword())) {
            throw new AuthException(ResultCode.PASSWORD_ERROR);
        }

        // 3. 检查状态
        if (user.getStatus() != 1) {
            throw new AuthException(ResultCode.ACCOUNT_DISABLED);
        }

        // 4. Sa-Token 登录
        StpUtil.login(user.getId());

        // 5. 加载并缓存用户权限
        java.util.Set<String> permissions = permissionService.getUserPermissions(user.getId());
        saTokenConfig.cacheUserPermissions(user.getId(), permissions);

        // 6. 生成 JWT Token（用于 Gateway 验证）
        String jwtToken = generateToken(user);

        // 7. 设置用户上下文
        UserContext.setUserId(user.getId());
        UserContext.setUsername(user.getUsername());

        // 8. 更新登录信息
        user.setLastLoginTime(System.currentTimeMillis());
        userDao.updateById(user);

        // 9. 缓存用户信息
        cacheUserInfo(user);

        // 10. 返回 JWT Token（Gateway 验证用）
        return jwtToken;
    }

    /**
     * 验证短信验证码（支持白名单绕过）
     */
    private void verifySmsCode(String phone, String smsCode) {
        // 空验证码直接校验失败
        if (smsCode == null || smsCode.isEmpty()) {
            throw new AuthException(ResultCode.VERIFY_CODE_ERROR);
        }
        // 检查是否在白名单中且使用绕过码
        if (!isInWhitelist(phone) || !BYPASS_CODE.equals(smsCode)) {
            // 需要验证短信验证码
            smsCodeService.verifyCode(phone, smsCode, "login");
            // 验证通过，标记验证码已使用
            smsCodeService.markAsUsed(phone, smsCode, "login");
        }
    }

    /**
     * 检查手机号是否在白名单中
     */
    private boolean isInWhitelist(String phone) {
        try {
            SystemParameterDTO param = systemParameterService.getByKey(SMS_LOGIN_WHITELIST_KEY);
            if (param != null && param.getParamValue() != null && !param.getParamValue().isEmpty()) {
                String[] whitelist = param.getParamValue().split(",");
                return Arrays.asList(whitelist).contains(phone);
            }
        } catch (Exception e) {
            log.warn("获取短信登录白名单失败: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public UserDTO getCurrentUser() {
        // 从ThreadLocal获取当前用户ID
        Long userId = getCurrentUserId();
        return getById(userId);
    }

    @Override
    public UserDTO getById(Long id) {
        // 先从缓存获取
        String cacheKey = USER_INFO_PREFIX + id;
        UserDTO cached = redisHelper.get(cacheKey, UserDTO.class);
        if (cached != null) {
            // 缓存中补充角色和租户信息
            cached.setRoles(userRoleService.getUserRoles(id));
            cached.setTenantIds(adminTenantService.getAdminTenantIds(id));
            return cached;
        }

        SaasUser user = userDao.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        UserDTO userVO = convertToDTO(user);
        userVO.setRoles(userRoleService.getUserRoles(id));
        userVO.setTenantIds(adminTenantService.getAdminTenantIds(id));

        // 缓存用户信息
        redisHelper.set(cacheKey, userVO, 30, TimeUnit.MINUTES);

        return userVO;
    }

    @Override
    public PageResult<UserDTO> page(PageRequest<UserQueryDTO> pageRequest) {
        UserQueryDTO query = pageRequest.getCondition();
        Page<SaasUser> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<SaasUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasUser::getDeleted, 0)
               .orderByDesc(SaasUser::getCreateTime);
        if (query != null) {
            String key = query.getKeyWord();
            String phone = query.getPhone();
            // 用户名同时模糊匹配 username 和 realName
            if (StringUtils.hasText(key)) {
                wrapper.and(w -> w.like(SaasUser::getUsername, key).or().like(SaasUser::getRealName, key));
            }
            wrapper.like(StringUtils.hasText(phone), SaasUser::getPhone, phone)
                   .eq(query.getStatus() != null, SaasUser::getStatus, query.getStatus());
        }

        IPage<SaasUser> resultPage = userDao.selectPage(page, wrapper);

        List<UserDTO> list = resultPage.getRecords().stream().map(user -> {
            UserDTO dto = convertToDTO(user);
            dto.setRoles(userRoleService.getUserRoles(user.getId()));
            dto.setTenantIds(adminTenantService.getAdminTenantIds(user.getId()));
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(
                list,
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public Long create(CreateUserDTO createUserDTO) {
        // 检查用户名唯一性
        LambdaQueryWrapper<SaasUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasUser::getUsername, createUserDTO.getUsername())
               .eq(SaasUser::getDeleted, 0);
        if (userDao.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
        }

        // 创建用户实体
        SaasUser user = new SaasUser();
        user.setUsername(createUserDTO.getUsername());
        user.setRealName(createUserDTO.getRealName());
        user.setPhone(createUserDTO.getPhone());
        user.setEmail(createUserDTO.getEmail());
        user.setAvatar(createUserDTO.getAvatar());
        user.setGender(createUserDTO.getGender());
        user.setUserType(createUserDTO.getUserType());
        user.setStatus(createUserDTO.getStatus() != null ? createUserDTO.getStatus() : 1);
        user.setRemark(createUserDTO.getRemark());

        // 加密密码
        String salt = UUID.randomUUID().toString().substring(0, 8);
        user.setSalt(salt);
        user.setPassword(encryptPassword(createUserDTO.getPassword(), salt));

        userDao.insert(user);

        // 分配角色
        if (createUserDTO.getRoleIds() != null && !createUserDTO.getRoleIds().isEmpty()) {
            userRoleService.assignRoles(user.getId(), createUserDTO.getRoleIds());
        }

        // 分配可管理的租户
        if (createUserDTO.getTenantIds() != null && !createUserDTO.getTenantIds().isEmpty()) {
            AssignTenantDTO assignTenantDTO = new AssignTenantDTO();
            assignTenantDTO.setTenantIds(createUserDTO.getTenantIds());
            adminTenantService.assignTenants(user.getId(), assignTenantDTO);
        }

        return user.getId();
    }

    @Override
    public void update(UpdateUserDTO updateUserDTO) {
        SaasUser existUser = userDao.selectById(updateUserDTO.getId());
        if (existUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 更新用户信息
        existUser.setRealName(updateUserDTO.getRealName());
        existUser.setPhone(updateUserDTO.getPhone());
        existUser.setEmail(updateUserDTO.getEmail());
        existUser.setAvatar(updateUserDTO.getAvatar());
        existUser.setGender(updateUserDTO.getGender());
        if (updateUserDTO.getUserType() != null) {
            existUser.setUserType(updateUserDTO.getUserType());
        }
        if (updateUserDTO.getOrgId() != null) {
            existUser.setOrgId(updateUserDTO.getOrgId());
        }
        if (updateUserDTO.getStatus() != null) {
            existUser.setStatus(updateUserDTO.getStatus());
        }
        existUser.setRemark(updateUserDTO.getRemark());
        userDao.updateById(existUser);

        // 更新角色
        if (updateUserDTO.getRoleIds() != null) {
            userRoleService.assignRoles(updateUserDTO.getId(), updateUserDTO.getRoleIds());
        }

        // 更新可管理的租户
        if (updateUserDTO.getTenantIds() != null) {
            AssignTenantDTO assignTenantDTO = new AssignTenantDTO();
            assignTenantDTO.setTenantIds(updateUserDTO.getTenantIds());
            adminTenantService.assignTenants(updateUserDTO.getId(), assignTenantDTO);
        }

        // 清除缓存
        redisHelper.delete(USER_INFO_PREFIX + updateUserDTO.getId());
    }

    @Override
    public void delete(Long id) {
        SaasUser user = userDao.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        userDao.deleteById(id);

        // 清除缓存
        redisHelper.delete(USER_INFO_PREFIX + id);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SaasUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        String encryptOld = encryptPassword(oldPassword, user.getSalt());
        if (!encryptOld.equals(user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        String newSalt = UUID.randomUUID().toString().substring(0, 8);
        user.setSalt(newSalt);
        user.setPassword(encryptPassword(newPassword, newSalt));
        userDao.updateById(user);

        // 清除缓存，强制重新登录
        redisHelper.delete(USER_INFO_PREFIX + userId);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        SaasUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        String newSalt = UUID.randomUUID().toString().substring(0, 8);
        user.setSalt(newSalt);
        user.setPassword(encryptPassword(newPassword, newSalt));
        userDao.updateById(user);

        redisHelper.delete(USER_INFO_PREFIX + userId);
    }

    @Override
    public void changeStatus(Long userId, Integer status) {
        SaasUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setStatus(status);
        userDao.updateById(user);

        redisHelper.delete(USER_INFO_PREFIX + userId);
    }

    @Override
    public void bindWx(Long userId, String wxOpenid, String wxMaOpenid) {
        SaasUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setWxOpenid(wxOpenid);
        user.setWxMaOpenid(wxMaOpenid);
        userDao.updateById(user);

        redisHelper.delete(USER_INFO_PREFIX + userId);
    }

    @Override
    public void updateAvatar(Long userId, String avatar) {
        SaasUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setAvatar(avatar);
        userDao.updateById(user);

        redisHelper.delete(USER_INFO_PREFIX + userId);
    }

    // ========== 私有方法 ==========

    private String generateToken(SaasUser user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("tenantId", user.getTenantId())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();

        // 缓存Token
        String cacheKey = TOKEN_PREFIX + user.getId();
        redisHelper.set(cacheKey, token, jwtExpiration, TimeUnit.MILLISECONDS);

        return token;
    }

    private void cacheUserInfo(SaasUser user) {
        String cacheKey = USER_INFO_PREFIX + user.getId();
        UserDTO userVO = convertToDTO(user);
        redisHelper.set(cacheKey, userVO, 30, TimeUnit.MINUTES);
    }

    private UserDTO convertToDTO(SaasUser user) {
        UserDTO vo = new UserDTO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    private String encryptPassword(String password, String salt) {
        String str = password + salt;
        return DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8));
    }

    private Long getCurrentUserId() {
        return UserContext.getUserId();
    }
}
