package com.lzlj.account.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * LZLJ 用户登录DTO
 */
@Data
public class LzljUserLoginDTO {

    @Schema(description = "用户名或手机号", example = "admin")
    private String username;

    @Schema(description = "密码", example = "admin123")
    private String password;

    @Schema(description = "短信验证码（双重验证模式必填）", example = "123456")
    private String smsCode;

    @Schema(description = "登录类型：1=管理员账号密码登录，2=用户手机号+密码+验证码登录", example = "1")
    private Integer loginType;
}
