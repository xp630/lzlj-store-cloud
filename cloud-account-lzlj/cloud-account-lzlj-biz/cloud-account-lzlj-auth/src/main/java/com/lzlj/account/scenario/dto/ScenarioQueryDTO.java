package com.lzlj.account.scenario.dto;

import com.lzlj.account.common.core.domain.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务场景查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "业务场景查询条件")
public class ScenarioQueryDTO extends PageQueryDTO {

    @Schema(description = "母商户ID")
    private Long merchantId;

    @Schema(description = "场景代码，如 B2B/B2C/C2C")
    private String scenarioCode;

    @Schema(description = "场景名称")
    private String scenarioName;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
}
