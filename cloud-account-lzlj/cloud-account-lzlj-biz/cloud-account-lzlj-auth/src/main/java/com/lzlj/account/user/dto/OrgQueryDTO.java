package com.lzlj.account.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 机构查询DTO
 */
@Data
@Schema(description = "机构查询条件")
public class OrgQueryDTO {

    @Schema(description = "上级机构ID（0表示根级）")
    private Long parentId;

    @Schema(description = "机构名称（模糊查询）")
    private String orgName;

    @Schema(description = "机构编码（精确查询）")
    private String orgCode;

    @Schema(description = "母户ID")
    private Long merchantId;

    @Schema(description = "母户名称（模糊查询）")
    private String merchantName;

    @Schema(description = "场景ID")
    private Long scenarioId;

    @Schema(description = "状态：0禁用 1启用")
    private Integer status;
}
