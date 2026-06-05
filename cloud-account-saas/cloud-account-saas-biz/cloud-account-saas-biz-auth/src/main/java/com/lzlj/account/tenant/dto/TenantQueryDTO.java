package com.lzlj.account.tenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户查询DTO
 */
@Data
@Schema(description = "租户查询条件")
public class TenantQueryDTO {

    @Schema(description = "关键字（租户名称/编码）")
    private String keyword;

    @Schema(description = "状态")
    private Integer status;
}
