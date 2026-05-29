package com.lzlj.account.paymentchannel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建支付通道请求DTO
 */
@Data
@Schema(description = "创建支付通道请求")
public class CreatePaymentChannelDTO {

    @NotBlank(message = "通道编码不能为空")
    @Schema(description = "通道编码（UNIONPAY/银联, NETBANK/网商）")
    private String channelCode;

    @NotBlank(message = "通道名称不能为空")
    @Schema(description = "通道名称（银联/网商）")
    private String channelName;

    @NotBlank(message = "支付方式不能为空")
    @Schema(description = "支付方式（逗号分隔，如 WECHAT,ALIPAY）")
    private String paymentMethod;

    @Schema(description = "云账户管理费率")
    private BigDecimal cloudAccountFee;

    @Schema(description = "上游成本费率")
    private BigDecimal upstreamCostFee;

    @Schema(description = "总费率成本（技术服务费）")
    private BigDecimal totalFeeCost;

    @Schema(description = "单笔限额")
    private BigDecimal perTransactionLimit;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
