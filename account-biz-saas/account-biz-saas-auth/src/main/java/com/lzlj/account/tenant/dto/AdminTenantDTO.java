package com.lzlj.account.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理员可管理租户DTO
 */
@Data
@Schema(description = "管理员可管理租户响应")
public class AdminTenantDTO {

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "租户编码")
    private String tenantCode;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "是否当前选中的租户")
    private Boolean selected;
}
