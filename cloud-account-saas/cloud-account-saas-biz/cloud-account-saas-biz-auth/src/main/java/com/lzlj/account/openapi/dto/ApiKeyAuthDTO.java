package com.lzlj.account.openapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * API密钥认证信息（内部接口，供网关调用）
 */
@Data
@Schema(description = "API密钥认证信息")
public class ApiKeyAuthDTO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "API公钥")
    private String apiKey;

    @Schema(description = "加密后的密钥")
    private String apiSecret;

    @Schema(description = "绑定的租户ID")
    private Long tenantId;

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;
}
