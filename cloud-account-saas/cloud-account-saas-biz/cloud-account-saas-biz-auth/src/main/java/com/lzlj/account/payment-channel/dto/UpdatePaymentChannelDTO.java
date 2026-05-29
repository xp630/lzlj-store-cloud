package com.lzlj.account.paymentchannel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 更新支付通道请求DTO
 */
@Data
@Schema(description = "更新支付通道请求")
public class UpdatePaymentChannelDTO {

    @NotNull(message = "云账户管理费率不能为空")
    @Schema(description = "云账户管理费率")
    private BigDecimal cloudAccountFee;

    @NotNull(message = "上游成本费率不能为空")
    @Schema(description = "上游成本费率")
    private BigDecimal upstreamCostFee;

    @NotNull(message = "总费率成本不能为空")
    @Schema(description = "总费率成本（技术服务费）")
    private BigDecimal totalFeeCost;

    @NotNull(message = "单笔限额不能为空")
    @Schema(description = "单笔限额")
    private BigDecimal perTransactionLimit;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
