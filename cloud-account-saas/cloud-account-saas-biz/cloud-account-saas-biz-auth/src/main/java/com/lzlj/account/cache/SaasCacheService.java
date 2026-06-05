package com.lzlj.account.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lzlj.account.common.core.constant.CacheConstants;
import com.lzlj.account.common.core.domain.datadictionary.DataDictionaryDTO;
import com.lzlj.account.common.core.helper.RedisHelper;
import com.lzlj.account.biz.menu.dto.MenuDTO;
import com.lzlj.account.biz.role.dto.RoleDTO;
import com.lzlj.account.biz.systemparameter.dto.SystemParameterDTO;
import com.lzlj.account.biz.tenant.dto.TenantDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SaaS 通用缓存服务
 * <p>
 * 提供菜单、数据字典、角色、系统参数等常用数据的缓存管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaasCacheService {

    private final RedisHelper redisHelper;

    // ==================== 菜单缓存 ====================

    /**
     * 获取菜单缓存
     */
    public List<MenuDTO> getMenus() {
        return redisHelper.get(CacheConstants.SAAS_MENUS_KEY, new TypeReference<List<MenuDTO>>() {});
    }

    /**
     * 设置菜单缓存
     */
    public void setMenus(List<MenuDTO> menus) {
        redisHelper.set(CacheConstants.SAAS_MENUS_KEY, menus,
                CacheConstants.MENUS_CACHE_MINUTES, java.util.concurrent.TimeUnit.MINUTES);
        log.debug("【缓存】设置菜单缓存: count={}", menus.size());
    }

    /**
     * 失效菜单缓存
     */
    public void invalidateMenus() {
        redisHelper.delete(CacheConstants.SAAS_MENUS_KEY);
        log.debug("【缓存】失效菜单缓存");
    }

    // ==================== 数据字典缓存 ====================

    /**
     * 获取数据字典（按类型）
     */
    public List<DataDictionaryDTO> getDataDictionaryByType(String dictType) {
        String key = CacheConstants.SAAS_DICT_TYPE_PREFIX + dictType;
        return redisHelper.get(key, new TypeReference<List<DataDictionaryDTO>>() {});
    }

    /**
     * 设置数据字典（按类型）
     */
    public void setDataDictionaryByType(String dictType, List<DataDictionaryDTO> dicts) {
        String key = CacheConstants.SAAS_DICT_TYPE_PREFIX + dictType;
        redisHelper.set(key, dicts, CacheConstants.DICT_CACHE_MINUTES, java.util.concurrent.TimeUnit.MINUTES);
        log.debug("【缓存】设置数据字典缓存: dictType={}, count={}", dictType, dicts.size());
    }

    /**
     * 获取所有数据字典分组
     */
    public Map<String, List<DataDictionaryDTO>> getAllDataDictionaryGroup() {
        return redisHelper.get(CacheConstants.SAAS_DICT_ALL_GROUP, new TypeReference<Map<String, List<DataDictionaryDTO>>>() {});
    }

    /**
     * 设置所有数据字典分组
     */
    public void setAllDataDictionaryGroup(Map<String, List<DataDictionaryDTO>> groupMap) {
        redisHelper.set(CacheConstants.SAAS_DICT_ALL_GROUP, groupMap,
                CacheConstants.DICT_CACHE_MINUTES, java.util.concurrent.TimeUnit.MINUTES);
        log.debug("【缓存】设置数据字典分组缓存: types={}", groupMap.keySet());
    }

    /**
     * 失效数据字典缓存
     */
    public void invalidateDataDictionary(String dictType) {
        if (dictType != null && !dictType.isEmpty()) {
            String key = CacheConstants.SAAS_DICT_TYPE_PREFIX + dictType;
            redisHelper.delete(key);
            log.debug("【缓存】失效数据字典缓存: dictType={}", dictType);
        } else {
            // 失效所有字典缓存（分组）
            redisHelper.delete(CacheConstants.SAAS_DICT_ALL_GROUP);
            log.debug("【缓存】失效所有数据字典缓存");
        }
    }

    // ==================== 角色缓存 ====================

    /**
     * 获取角色缓存
     */
    public List<RoleDTO> getRoles() {
        return redisHelper.get(CacheConstants.SAAS_ROLES_KEY, new TypeReference<List<RoleDTO>>() {});
    }

    /**
     * 设置角色缓存
     */
    public void setRoles(List<RoleDTO> roles) {
        redisHelper.set(CacheConstants.SAAS_ROLES_KEY, roles,
                CacheConstants.ROLES_CACHE_MINUTES, java.util.concurrent.TimeUnit.MINUTES);
        log.debug("【缓存】设置角色缓存: count={}", roles.size());
    }

    /**
     * 失效角色缓存
     */
    public void invalidateRoles() {
        redisHelper.delete(CacheConstants.SAAS_ROLES_KEY);
        log.debug("【缓存】失效角色缓存");
    }

    // ==================== 系统参数缓存 ====================

    /**
     * 获取系统参数缓存
     */
    public List<SystemParameterDTO> getSystemParameters() {
        return redisHelper.get(CacheConstants.SAAS_SYS_PARAM_ALL, new TypeReference<List<SystemParameterDTO>>() {});
    }

    /**
     * 设置系统参数缓存
     */
    public void setSystemParameters(List<SystemParameterDTO> params) {
        redisHelper.set(CacheConstants.SAAS_SYS_PARAM_ALL, params,
                CacheConstants.SYS_PARAM_CACHE_MINUTES, java.util.concurrent.TimeUnit.MINUTES);
        log.debug("【缓存】设置系统参数缓存: count={}", params.size());
    }

    /**
     * 失效系统参数缓存
     */
    public void invalidateSystemParameters() {
        redisHelper.delete(CacheConstants.SAAS_SYS_PARAM_ALL);
        log.debug("【缓存】失效系统参数缓存");
    }

    // ==================== 租户缓存 ====================

    /**
     * 获取用户角色缓存
     */
    public List<RoleDTO> getUserRoles(Long userId) {
        String key = CacheConstants.SAAS_USER_ROLES_PREFIX + userId;
        return redisHelper.get(key, new TypeReference<List<RoleDTO>>() {});
    }

    /**
     * 设置用户角色缓存
     */
    public void setUserRoles(Long userId, List<RoleDTO> roles) {
        String key = CacheConstants.SAAS_USER_ROLES_PREFIX + userId;
        redisHelper.set(key, roles, CacheConstants.USER_ROLES_CACHE_MINUTES, TimeUnit.MINUTES);
        log.debug("【缓存】设置用户角色缓存: userId={}, count={}", userId, roles.size());
    }

    /**
     * 失效用户角色缓存
     */
    public void invalidateUserRoles(Long userId) {
        String key = CacheConstants.SAAS_USER_ROLES_PREFIX + userId;
        redisHelper.delete(key);
        log.debug("【缓存】失效用户角色缓存: userId={}", userId);
    }

    /**
     * 获取租户缓存
     */
    public TenantDTO getTenant(Long tenantId) {
        String key = CacheConstants.SAAS_TENANT_PREFIX + tenantId;
        return redisHelper.get(key, TenantDTO.class);
    }

    /**
     * 设置租户缓存
     */
    public void setTenant(Long tenantId, TenantDTO tenant) {
        String key = CacheConstants.SAAS_TENANT_PREFIX + tenantId;
        redisHelper.set(key, tenant, CacheConstants.TENANT_CACHE_MINUTES, java.util.concurrent.TimeUnit.MINUTES);
        log.debug("【缓存】设置租户缓存: tenantId={}", tenantId);
    }

    /**
     * 失效租户缓存
     */
    public void invalidateTenant(Long tenantId) {
        String key = CacheConstants.SAAS_TENANT_PREFIX + tenantId;
        redisHelper.delete(key);
        log.debug("【缓存】失效租户缓存: tenantId={}", tenantId);
    }
}
