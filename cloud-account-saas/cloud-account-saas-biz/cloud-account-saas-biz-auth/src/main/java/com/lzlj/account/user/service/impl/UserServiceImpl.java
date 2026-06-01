package com.lzlj.account.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.AuthException;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.user.dao.UserDao;
import com.lzlj.account.user.dto.UserLoginDTO;
import com.lzlj.account.user.entity.User;
import com.lzlj.account.user.service.UserService;
import com.lzlj.account.user.dto.UserDTO;
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
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    private static final String TOKEN_PREFIX = "token:";
    private static final String USER_INFO_PREFIX = "user:info:";

    @Override
    public String login(UserLoginDTO loginDTO) {
        // 1. 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername())
               .eq(User::getDeleted, 0);
        User user = userDao.selectOne(wrapper);

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

        // 6. 缓存用户信息
        cacheUserInfo(user);

        return token;
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
        UserDTO cachedUser = (UserDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }

        User user = userDao.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        UserDTO userVO = convertToDTO(user);

        // 缓存用户信息
        redisTemplate.opsForValue().set(cacheKey, userVO, 30, TimeUnit.MINUTES);

        return userVO;
    }

    @Override
    public PageResult<UserDTO> page(Long orgId, String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper
               .like(keyword != null, User::getUsername, keyword)
               .eq(status != null, User::getStatus, status)
               .eq(User::getDeleted, 0)
               .orderByDesc(User::getCreateTime);

        IPage<User> resultPage = userDao.selectPage(page, wrapper);

        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public Long create(User user) {
        // 检查用户名唯一性
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername())
               .eq(User::getDeleted, 0);
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
    public void update(User user) {
        User existUser = userDao.selectById(user.getId());
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
        User user = userDao.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        userDao.deleteById(id);

        // 清除缓存
        redisTemplate.delete(USER_INFO_PREFIX + id);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userDao.selectById(userId);
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
        User user = userDao.selectById(userId);
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
        User user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setStatus(status);
        userDao.updateById(user);

        redisTemplate.delete(USER_INFO_PREFIX + userId);
    }

    @Override
    public void bindWx(Long userId, String wxOpenid, String wxMaOpenid) {
        User user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setWxOpenid(wxOpenid);
        user.setWxMaOpenid(wxMaOpenid);
        userDao.updateById(user);

        redisTemplate.delete(USER_INFO_PREFIX + userId);
    }

    @Override
    public void updateAvatar(Long userId, String avatar) {
        User user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        user.setAvatar(avatar);
        userDao.updateById(user);

        redisTemplate.delete(USER_INFO_PREFIX + userId);
    }

    // ========== 私有方法 ==========

    private String generateToken(User user) {
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
        redisTemplate.opsForValue().set(cacheKey, token, jwtExpiration, TimeUnit.MILLISECONDS);

        return token;
    }

    private void cacheUserInfo(User user) {
        String cacheKey = USER_INFO_PREFIX + user.getId();
        UserDTO userVO = convertToDTO(user);
        redisTemplate.opsForValue().set(cacheKey, userVO, 30, TimeUnit.MINUTES);
    }

    private UserDTO convertToDTO(User user) {
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
