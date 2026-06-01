package com.lzlj.account.scenario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 更新业务场景DTO
 */
@Data
@Schema(description = "更新业务场景请求")
public class UpdateScenarioDTO {

    @Schema(description = "场景名称")
    private String scenarioName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    @Schema(description = "关联的支付通道ID列表")
    private List<Long> channelIds;
}
