package com.lzlj.account.scenario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建业务场景DTO
 */
@Data
@Schema(description = "创建业务场景请求")
public class CreateScenarioDTO {

    @NotNull(message = "母商户ID不能为空")
    @Schema(description = "母商户ID")
    private Long merchantId;

    @NotBlank(message = "场景代码不能为空")
    @Schema(description = "场景代码，如 B2B/B2C/C2C")
    private String scenarioCode;

    @NotBlank(message = "场景名称不能为空")
    @Schema(description = "场景名称")
    private String scenarioName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "关联的支付通道ID列表")
    private List<Long> channelIds;
}
