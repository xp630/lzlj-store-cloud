package com.lzlj.account.common.core.constant;

/**
 * 缓存 Key 常量
 */
public class CacheConstants {

    private CacheConstants() {}

    // ==================== SaaS 缓存 Key ====================

    /**
     * SaaS 菜单缓存（全局，无租户隔离）
     * Key: saas:cache:menus
     */
    public static final String SAAS_MENUS_KEY = "saas:cache:menus";

    /**
     * SaaS 角色缓存（全局，无租户隔离）
     * Key: saas:cache:roles
     */
    public static final String SAAS_ROLES_KEY = "saas:cache:roles";

    /**
     * SaaS 数据字典缓存（按类型）
     * Key: saas:cache:dict:type:{dictType}
     */
    public static final String SAAS_DICT_TYPE_PREFIX = "saas:cache:dict:type:";

    /**
     * SaaS 数据字典分组缓存
     * Key: saas:cache:dict:all-group
     */
    public static final String SAAS_DICT_ALL_GROUP = "saas:cache:dict:all-group";

    /**
     * SaaS 系统参数缓存
     * Key: saas:cache:sys-param:all
     */
    public static final String SAAS_SYS_PARAM_ALL = "saas:cache:sys-param:all";

    /**
     * SaaS 租户缓存（按ID）
     * Key: saas:cache:tenant:{id}
     */
    public static final String SAAS_TENANT_PREFIX = "saas:cache:tenant:";

    /**
     * SaaS 用户角色缓存（按用户ID）
     * Key: saas:cache:user:roles:{userId}
     */
    public static final String SAAS_USER_ROLES_PREFIX = "saas:cache:user:roles:";

    // ==================== 缓存 TTL ====================

    /**
     * 菜单缓存时间（分钟）
     */
    public static final long MENUS_CACHE_MINUTES = 30;

    /**
     * 角色缓存时间（分钟）
     */
    public static final long ROLES_CACHE_MINUTES = 30;

    /**
     * 数据字典缓存时间（分钟）
     */
    public static final long DICT_CACHE_MINUTES = 60;

    /**
     * 系统参数缓存时间（分钟）
     */
    public static final long SYS_PARAM_CACHE_MINUTES = 60;

    /**
     * 租户缓存时间（分钟）
     */
    public static final long TENANT_CACHE_MINUTES = 30;

    /**
     * 用户角色缓存时间（分钟）
     */
    public static final long USER_ROLES_CACHE_MINUTES = 30;
}