package com.lzlj.account.openapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建API密钥请求DTO
 */
@Data
@Schema(description = "创建API密钥请求")
public class CreateApiKeyDTO {

    @NotNull(message = "租户ID不能为空")
    @Schema(description = "绑定的租户ID")
    private Long tenantId;

    @NotBlank(message = "API名称不能为空")
    @Schema(description = "API名称")
    private String name;

    @Schema(description = "API描述")
    private String description;

    @Schema(description = "速率限制（次/分钟）")
    private Integer rateLimit;

    @Schema(description = "过期时间（NULL表示永不过期）")
    private java.time.LocalDateTime expiresTime;
}
