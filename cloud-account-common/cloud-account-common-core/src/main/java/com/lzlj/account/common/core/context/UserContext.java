package com.lzlj.account.common.core.context;

/**
 * 用户上下文工具类
 * 使用 ThreadLocal 持有当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<Long> ORG_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ORG_NAME = new ThreadLocal<>();
    private static final ThreadLocal<String> FUNCTIONAL_ROLES = new ThreadLocal<>();
    private static final ThreadLocal<String> DATA_ROLES = new ThreadLocal<>();

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
     * 设置当前机构名称
     */
    public static void setOrgName(String orgName) {
        ORG_NAME.set(orgName);
    }

    /**
     * 获取当前机构名称
     */
    public static String getOrgName() {
        return ORG_NAME.get();
    }

    /**
     * 设置功能角色列表（逗号分隔）
     */
    public static void setFunctionalRoles(String functionalRoles) {
        FUNCTIONAL_ROLES.set(functionalRoles);
    }

    /**
     * 获取功能角色列表（逗号分隔）
     */
    public static String getFunctionalRoles() {
        return FUNCTIONAL_ROLES.get();
    }

    /**
     * 设置数据角色列表（逗号分隔）
     */
    public static void setDataRoles(String dataRoles) {
        DATA_ROLES.set(dataRoles);
    }

    /**
     * 获取数据角色列表（逗号分隔）
     */
    public static String getDataRoles() {
        return DATA_ROLES.get();
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ORG_ID.remove();
        ORG_NAME.remove();
        FUNCTIONAL_ROLES.remove();
        DATA_ROLES.remove();
    }
}
