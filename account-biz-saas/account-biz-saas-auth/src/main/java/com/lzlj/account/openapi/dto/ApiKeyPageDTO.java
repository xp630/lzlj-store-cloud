package com.lzlj.account.openapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新API密钥请求DTO
 */
@Data
@Schema(description = "更新API密钥请求")
public class UpdateApiKeyDTO {

    @NotNull(message = "API名称不能为空")
    @Schema(description = "API名称")
    private String name;

    @Schema(description = "API描述")
    private String description;

    @Schema(description = "速率限制（次/分钟）")
    private Integer rateLimit;

    @Schema(description = "过期时间")
    private java.time.LocalDateTime expiresTime;

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;
}
