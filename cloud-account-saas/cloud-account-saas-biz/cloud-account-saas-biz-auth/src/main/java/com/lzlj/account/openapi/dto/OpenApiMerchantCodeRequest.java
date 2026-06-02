package com.lzlj.account.openapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * OpenAPI 商户编码请求
 */
@Data
@Schema(description = "商户编码请求")
public class OpenApiMerchantCodeRequest {

    @NotBlank(message = "商户编码不能为空")
    @Schema(description = "商户编码", required = true)
    private String merchantCode;
}
