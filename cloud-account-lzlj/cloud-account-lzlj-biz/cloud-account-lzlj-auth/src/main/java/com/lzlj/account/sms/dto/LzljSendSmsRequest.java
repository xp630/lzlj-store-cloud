package com.lzlj.account.sms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 发送短信请求
 */
@Data
@Schema(description = "发送短信请求")
public class LzljSendSmsRequest {

    @Schema(description = "手机号", example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @Schema(description = "类型: login/register/reset_pwd", example = "login")
    @NotBlank(message = "类型不能为空")
    private String type;
}
