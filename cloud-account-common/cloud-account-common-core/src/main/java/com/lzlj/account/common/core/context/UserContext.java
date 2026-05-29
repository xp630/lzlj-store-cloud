package com.lzlj.account.common.core.context;

/**
 * 用户上下文工具类
 * 使用 ThreadLocal 持有当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<Long> ORG_ID = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 设置当前用户名
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        return USERNAME.get();
    }

    /**
     * 设置当前机构ID
     */
    public static void setOrgId(Long orgId) {
        ORG_ID.set(orgId);
    }

    /**
     * 获取当前机构ID
     */
    public static Long getOrgId() {
        return ORG_ID.get();
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ORG_ID.remove();
    }
}
