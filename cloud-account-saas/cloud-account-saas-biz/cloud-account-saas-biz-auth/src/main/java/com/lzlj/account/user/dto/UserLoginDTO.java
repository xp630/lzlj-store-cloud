package com.lzlj.account.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录请求
 */
@Data
@Schema(description = "用户登录请求")
public class UserLoginDTO {

    @Schema(description = "用户名或手机号", example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "验证码（图片验证码模式必填）", example = "abcd")
    private String verifyCode;

    @Schema(description = "租户ID（多租户模式必填）", example = "1")
    private Long tenantId;

    @Schema(description = "登录方式：1=账号密码登录，2=微信登录", example = "1")
    private Integer loginType;
}
