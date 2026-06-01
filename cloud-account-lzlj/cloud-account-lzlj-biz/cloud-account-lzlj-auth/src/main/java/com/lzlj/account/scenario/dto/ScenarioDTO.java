package com.lzlj.account.scenario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 业务场景DTO
 */
@Data
@Schema(description = "业务场景详情")
public class ScenarioDTO {

    @Schema(description = "场景ID")
    private Long id;

    @Schema(description = "母商户ID")
    private Long merchantId;

    @Schema(description = "母商户名称")
    private String merchantName;

    @Schema(description = "场景代码，如 B2B/B2C/C2C")
    private String scenarioCode;

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

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
