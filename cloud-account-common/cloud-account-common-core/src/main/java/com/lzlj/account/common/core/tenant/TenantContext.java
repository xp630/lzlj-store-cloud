package com.lzlj.account.common.core.tenant;

/**
 * 租户上下文工具类
 * 使用 ThreadLocal 持有当前请求的租户ID和忽略标志
 */
public class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> IGNORE_TENANT = new ThreadLocal<>();

    /**
     * 设置当前租户ID
     */
    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * 获取当前租户ID
     */
    public static Long getTenantId() {
        Long tenantId = TENANT_ID.get();
        // dev环境如果没有租户ID，使用默认租户1
        if (tenantId == null) {
            return 1L;
        }
        return tenantId;
    }

    /**
     * 设置是否忽略租户隔离（优先级高于 ignoreTable 判断）
     */
    public static void setIgnoreTenant(Boolean ignore) {
        IGNORE_TENANT.set(ignore);
    }

    /**
     * 是否忽略租户隔离
     */
    public static Boolean isIgnoreTenant() {
        return IGNORE_TENANT.get();
    }

    /**
     * 清除租户上下文
     */
    public static void clear() {
        TENANT_ID.remove();
        IGNORE_TENANT.remove();
    }
}