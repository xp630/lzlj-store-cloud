package com.lzlj.account.common.core.tenant;

/**
 * 租户上下文工具类
 * 使用 ThreadLocal 持有当前请求的租户ID
 */
public class TenantContext {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();

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
        return TENANT_ID.get();
    }

    /**
     * 清除租户上下文
     */
    public static void clear() {
        TENANT_ID.remove();
    }
}
