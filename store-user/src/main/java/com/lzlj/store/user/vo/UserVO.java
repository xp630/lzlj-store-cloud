package com.lzlj.store.user.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户VO
 */
@Data
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别 0:未知 1:男 2:女
     */
    private Integer gender;

    /**
     * 状态 0:禁用 1:启用 2:锁定
     */
    private Integer status;

    /**
     * 用户类型 1:超级管理员 2:管理员 3:普通用户
     */
    private Integer userType;

    /**
     * 组织ID
     */
    private Long orgId;

    /**
     * 组织名称
     */
    private String orgName;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}
