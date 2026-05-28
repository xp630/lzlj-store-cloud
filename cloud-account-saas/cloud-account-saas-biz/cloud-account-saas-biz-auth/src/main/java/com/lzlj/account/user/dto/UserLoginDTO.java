package com.lzlj.account.user.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录DTO
 */
@Data
public class UserLoginDTO {

    /**
     * 用户名/手机号
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 登录类型 1:账号密码 2:微信
     */
    private Integer loginType;
}
