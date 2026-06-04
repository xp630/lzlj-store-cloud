package com.lzlj.account.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.AuthException;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.helper.RedisHelper;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.config.LzljSaTokenConfig;
import com.lzlj.account.permission.service.LzljPermissionService;
import com.lzlj.account.role.dto.LzljRoleDTO;
import com.lzlj.account.sms.service.LzljSmsCodeService;
import com.lzlj.account.systemparameter.dto.LzljSystemParameterDTO;
import com.lzlj.account.systemparameter.service.LzljSystemParameterService;
import com.lzlj.account.user.dto.LzljUserDTO;
import com.lzlj.account.user.dto.LzljUserLoginDTO;
import com.lzlj.account.user.entity.LzljUser;
import com.lzlj.account.user.dao.LzljUserDao;
import com.lzlj.account.user.service.LzljUserRoleService;
import com.lzlj.account.user.service.LzljUserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * LZLJ 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljUserServiceImpl implements LzljUserService {

    private final LzljUserDao userDao;
    private final RedisHelper redisHelper;
    private final LzljSmsCodeService smsCodeService;
    private final LzljSystemParameterService systemParameterService;
    private final LzljPermissionService permissionService;
    private final LzljSaTokenConfig saTokenConfig;
    private final LzljUserRoleService userRoleService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    private static final String TOKEN_PREFIX = "lzlj:token:";
    private static final String USER_INFO_PREFIX = "lzlj:user:info:";
    private static final String SMS_LOGIN_WHITELIST_KEY = "sms_login_whitelist";
    private static final String BYPASS_CODE = "000000";

    @Override
    public String login(LzljUserLoginDTO loginDTO) {
        LzljUser user;

        // 1. 根据 loginType 判断登录方式
        // loginType = 2 → 用户：手机号 + 密码 + 验证码
        // 其他 → 管理员：用户名 + 密码
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

    @Override
    public LzljUserDTO getCurrentUser() {
        Long userId = getCurrentUserId();
        return getById(userId);
    }

    @Override
    public LzljUserDTO getById(Long id) {
        // 先从缓存获取
        String cacheKey = USER_INFO_PREFIX + id;
        LzljUserDTO cached = redisHelper.get(cacheKey, LzljUserDTO.class);
        if (cached != null) {
            return cached;
        }

        LzljUser user = userDao.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        LzljUserDTO userVO = convertToDTO(user);

        // 缓存用户信息
        redisHelper.set(cacheKey, userVO, 30, TimeUnit.MINUTES);

        return userVO;
    }

    @Override
    public PageResult<LzljUserDTO> page(Long orgId, String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<LzljUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LzljUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(orgId != null, LzljUser::getOrgId, orgId)
               .like(keyword != null, LzljUser::getUsername, keyword)
               .eq(status != null, LzljUser::getStatus, status)
               .eq(LzljUser::getDeleted, 0)
               .orderByDesc(LzljUser::getCreateTime);

        IPage<LzljUser> resultPage = userDao.selectPage(page, wrapper);

        List<LzljUserDTO> list = resultPage.getRecords().stream().map(user -> {
            LzljUserDTO dto = convertToDTO(user);
            dto.setRoles(userRoleService.getUserRoles(user.getId()));
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
    public Long create(LzljUser user) {
        // 检查用户名唯一性
        LambdaQueryWrapper<LzljUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljUser::getUsername, user.getUsername())
               .eq(LzljUser::getDeleted, 0);
        if (userDao.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
        }

        // 加密密码
        String salt = UUID.randomUUID().toString().substring(0, 8);
        user.setSalt(salt);
        user.setPassword(encryptPassword(user.getPassword(), salt));
        user.setStatus(1);

        userDao.insert(user);
        return user.getId();
    }

    @Override
    public void update(LzljUser user) {
        LzljUser existUser = userDao.selectById(user.getId());
        if (existUser == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setPassword(null);
        user.setSalt(null);
        userDao.updateById(user);

        // 清除缓存
        redisHelper.delete(USER_INFO_PREFIX + user.getId());
    }

    @Override
    public void delete(Long id) {
        LzljUser user = userDao.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        userDao.deleteById(id);

        // 清除缓存
        redisHelper.delete(USER_INFO_PREFIX + id);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        LzljUser user = userDao.selectById(userId);
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
        LzljUser user = userDao.selectById(userId);
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
        LzljUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setStatus(status);
        userDao.updateById(user);

        redisHelper.delete(USER_INFO_PREFIX + userId);
    }

    @Override
    public void updateAvatar(Long userId, String avatar) {
        LzljUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setAvatar(avatar);
        userDao.updateById(user);

        redisHelper.delete(USER_INFO_PREFIX + userId);
    }

    // ========== 私有方法 ==========

    private String generateToken(LzljUser user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpiration);

        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("orgId", user.getOrgId())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();

        // 缓存Token
        String cacheKey = TOKEN_PREFIX + user.getId();
        redisHelper.set(cacheKey, token, jwtExpiration, TimeUnit.MILLISECONDS);

        return token;
    }

    private void cacheUserInfo(LzljUser user) {
        String cacheKey = USER_INFO_PREFIX + user.getId();
        LzljUserDTO userVO = convertToDTO(user);
        redisHelper.set(cacheKey, userVO, 30, TimeUnit.MINUTES);
    }

    private LzljUserDTO convertToDTO(LzljUser user) {
        LzljUserDTO vo = new LzljUserDTO();
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

    /**
     * 检查手机号是否在短信登录白名单中
     */
    private boolean isInWhitelist(String phone) {
        try {
            LzljSystemParameterDTO param = systemParameterService.getByKey(SMS_LOGIN_WHITELIST_KEY);
            if (param != null && param.getParamValue() != null && !param.getParamValue().isEmpty()) {
                String[] whitelist = param.getParamValue().split(",");
                return Arrays.asList(whitelist).contains(phone);
            }
        } catch (Exception e) {
            log.warn("获取短信登录白名单失败: {}", e.getMessage());
        }
        return false;
    }
}
