package com.lzlj.store.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.store.common.core.domain.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends TenantEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密）
     */
    private String password;

    /**
     * 盐值
     */
    private String salt;

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
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private Long lastLoginTime;

    /**
     * 微信OpenID
     */
    private String wxOpenid;

    /**
     * 微信小程序OpenID
     */
    private String wxMaOpenid;
}
