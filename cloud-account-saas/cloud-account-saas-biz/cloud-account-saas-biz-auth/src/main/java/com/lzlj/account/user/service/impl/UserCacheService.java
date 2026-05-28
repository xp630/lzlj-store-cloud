package com.lzlj.account.user.service.impl;

import com.lzlj.account.user.dao.UserDao;
import com.lzlj.account.user.dto.UserDTO;
import com.lzlj.account.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 旁路缓存（Cache-Aside）示例
 *
 * 核心流程：
 * 1. 读：先查缓存 -> 未命中 -> 查DB -> 写入缓存 -> 返回
 * 2. 写：先更新DB -> 删除缓存（而非更新）
 * 3. 删：直接删除DB -> 删除缓存
 *
 * @author lzlj
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final RedissonClient redissonClient;
    private final UserDao userDao;

    /**
     * 缓存 key
     */
    private static final String CACHE_KEY = "user_cache";

    /**
     * 缓存 TTL
     */
    private static final long CACHE_TTL = 5;

    /**
     * TimeUnit
     */
    private static final TimeUnit CACHE_UNIT = TimeUnit.MINUTES;

    // ==================== 核心旁路缓存操作 ====================

    /**
     * 旁路缓存 - 读取
     *
     * @param id 用户ID
     * @return 用户DTO
     */
    public UserDTO getById(Long id) {
        // 1. 先查缓存
        RMap<Long, UserDTO> cache = redissonClient.getMap(CACHE_KEY);
        UserDTO cachedUser = cache.get(id);

        if (cachedUser != null) {
            log.debug("【旁路缓存】命中缓存 userId={}", id);
            return cachedUser;
        }

        // 2. 缓存未命中，查数据库
        log.debug("【旁路缓存】未命中，查数据库 userId={}", id);
        User user = userDao.selectById(id);

        if (user != null) {
            UserDTO userDTO = convertToDTO(user);
            // 3. 写入缓存
            cache.put(id, userDTO);
            cache.expire(CACHE_TTL, CACHE_UNIT);
            log.debug("【旁路缓存】写入缓存 userId={}", id);
            return userDTO;
        }

        return null;
    }

    /**
     * 旁路缓存 - 通用模板方法
     *
     * @param key      缓存key
     * @param id       业务ID
     * @param dbLoader 数据库加载函数
     * @param <T>      缓存对象类型
     * @return 缓存对象
     */
    public <T> T get(String key, Object id, Supplier<T> dbLoader) {
        RMap<Object, T> cache = redissonClient.getMap(key);
        T cached = cache.get(id);

        if (cached != null) {
            log.debug("【旁路缓存】命中 key={}, id={}", key, id);
            return cached;
        }

        log.debug("【旁路缓存】未命中 key={}, id={}，加载数据库", key, id);
        T result = dbLoader.get();

        if (result != null) {
            cache.put(id, result);
            cache.expire(CACHE_TTL, CACHE_UNIT);
        }

        return result;
    }

    /**
     * 旁路缓存 - 更新（删除而非更新）
     *
     * @param id   用户ID
     * @param user 更新后的用户
     */
    public void updateById(Long id, User user) {
        // 1. 先更新数据库
        userDao.updateById(user);

        // 2. 删除缓存（而非更新）
        RMap<Long, UserDTO> cache = redissonClient.getMap(CACHE_KEY);
        cache.remove(id);

        log.debug("【旁路缓存】更新后删除缓存 userId={}", id);
    }

    /**
     * 旁路缓存 - 删除
     *
     * @param id 用户ID
     */
    public void deleteById(Long id) {
        // 1. 先删除数据库
        userDao.deleteById(id);

        // 2. 删除缓存
        RMap<Long, UserDTO> cache = redissonClient.getMap(CACHE_KEY);
        cache.remove(id);

        log.debug("【旁路缓存】删除缓存 userId={}", id);
    }

    // ==================== 高级模式 ====================

    /**
     * 延迟双删（解决缓存一致性）
     *
     * 在更新数据库后，延迟一段时间再次删除缓存
     * 防止在删除缓存的瞬间，有另一个请求查到了旧数据并写入了缓存
     *
     * @param id   用户ID
     * @param user 更新后的用户
     */
    public void updateWithDoubleDelete(Long id, User user) {
        // 1. 先删除缓存
        RMap<Long, UserDTO> cache = redissonClient.getMap(CACHE_KEY);
        cache.remove(id);

        // 2. 更新数据库
        userDao.updateById(user);

        // 3. 延迟双删 - 异步延迟删除缓存
        new Thread(() -> {
            try {
                Thread.sleep(100); // 延迟 100ms
                cache.remove(id);
                log.debug("【旁路缓存】延迟双删，删除缓存 userId={}", id);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * 缓存穿透防护 - 空值缓存
     *
     * 当查询结果为空时，也放入缓存，但值为 null
     * 防止大量请求查询不存在的数据直接打到数据库
     *
     * @param id 用户ID
     * @return 用户DTO（可能为null）
     */
    public UserDTO getByIdWith穿透Protection(Long id) {
        RMap<Long, UserDTO> cache = redissonClient.getMap(CACHE_KEY);

        // 1. 先查缓存
        UserDTO cachedUser = cache.get(id);

        if (cachedUser != null) {
            // 空值也说明命中了（穿透防护）
            log.debug("【旁路缓存】命中缓存（包括空值）userId={}", id);
            return cachedUser;
        }

        // 2. 查数据库
        User user = userDao.selectById(id);
        UserDTO userDTO = user != null ? convertToDTO(user) : null;

        // 3. 即使为空也写入缓存，防止穿透
        cache.put(id, userDTO);
        cache.expire(CACHE_TTL, CACHE_UNIT);

        return userDTO;
    }

    // ==================== 工具方法 ====================

    private UserDTO convertToDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setGender(user.getGender());
        dto.setStatus(user.getStatus());
        dto.setUserType(user.getUserType());
        dto.setTenantId(user.getTenantId());
        dto.setOrgId(user.getOrgId());
        return dto;
    }
}
