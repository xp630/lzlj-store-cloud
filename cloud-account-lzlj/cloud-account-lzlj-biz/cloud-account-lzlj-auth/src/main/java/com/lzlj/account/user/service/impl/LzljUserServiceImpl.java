package com.lzlj.account.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.AuthException;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.user.dto.LzljUserDTO;
import com.lzlj.account.user.dto.LzljUserLoginDTO;
import com.lzlj.account.user.entity.LzljUser;
import com.lzlj.account.user.mapper.LzljUserDao;
import com.lzlj.account.user.service.LzljUserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
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
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    private static final String TOKEN_PREFIX = "lzlj:token:";
    private static final String USER_INFO_PREFIX = "lzlj:user:info:";

    @Override
    public String login(LzljUserLoginDTO loginDTO) {
        // 1. 查询用户
        LambdaQueryWrapper<LzljUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljUser::getUsername, loginDTO.getUsername())
               .eq(LzljUser::getDeleted, 0);
        LzljUser user = userDao.selectOne(wrapper);

        if (user == null) {
            throw new AuthException(ResultCode.ACCOUNT_DISABLED);
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

        // 4. 生成Token
        String token = generateToken(user);

        // 5. 设置用户上下文
        UserContext.setUserId(user.getId());
        UserContext.setUsername(user.getUsername());

        // 6. 更新登录信息
        user.setLastLoginTime(System.currentTimeMillis());
        userDao.updateById(user);

        // 7. 缓存用户信息
        cacheUserInfo(user);

        return token;
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
        LzljUserDTO cachedUser = (LzljUserDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }

        LzljUser user = userDao.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        LzljUserDTO userVO = convertToDTO(user);

        // 缓存用户信息
        redisTemplate.opsForValue().set(cacheKey, userVO, 30, TimeUnit.MINUTES);

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

        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
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
        redisTemplate.delete(USER_INFO_PREFIX + user.getId());
    }

    @Override
    public void delete(Long id) {
        LzljUser user = userDao.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        userDao.deleteById(id);

        // 清除缓存
        redisTemplate.delete(USER_INFO_PREFIX + id);
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
        redisTemplate.delete(USER_INFO_PREFIX + userId);
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

        redisTemplate.delete(USER_INFO_PREFIX + userId);
    }

    @Override
    public void changeStatus(Long userId, Integer status) {
        LzljUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setStatus(status);
        userDao.updateById(user);

        redisTemplate.delete(USER_INFO_PREFIX + userId);
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
        redisTemplate.opsForValue().set(cacheKey, token, jwtExpiration, TimeUnit.MILLISECONDS);

        return token;
    }

    private void cacheUserInfo(LzljUser user) {
        String cacheKey = USER_INFO_PREFIX + user.getId();
        LzljUserDTO userVO = convertToDTO(user);
        redisTemplate.opsForValue().set(cacheKey, userVO, 30, TimeUnit.MINUTES);
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
}
