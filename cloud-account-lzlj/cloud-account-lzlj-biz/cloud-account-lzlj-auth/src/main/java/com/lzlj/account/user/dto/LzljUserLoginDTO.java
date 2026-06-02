package com.lzlj.account.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * LZLJ 用户登录DTO
 */
@Data
public class LzljUserLoginDTO {

    @Schema(description = "用户名（手机号）", example = "13800138000")
    private String username;

    @Schema(description = "密码", example = "admin123")
    private String password;

    @Schema(description = "短信验证码（双重验证模式必填）", example = "123456")
    private String smsCode;
}
