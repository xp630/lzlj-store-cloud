package com.lzlj.account.common.core.domain.paymentchannel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 统一支付通道DTO（适用于LZLJ和SaaS平台）
 * SaaS端管理完整的费率配置（cloudAccountFee, upstreamCostFee, totalFeeCost）
 * LZLJ端通过同步获取基础费率信息（feeRate, minAmount, maxAmount）
 */
@Data
@Schema(description = "支付通道响应")
public class PaymentChannelDTO {

    @Schema(description = "通道ID")
    private Long id;

    @Schema(description = "通道编码（UNIONPAY/银联, NETBANK/网商）")
    private String channelCode;

    @Schema(description = "通道名称（银联/网商）")
    private String channelName;

    @Schema(description = "支付方式（逗号分隔，如 WECHAT,ALIPAY）")
    private String paymentMethod;

    @Schema(description = "费率")
    private BigDecimal feeRate;

    @Schema(description = "最低交易金额")
    private BigDecimal minAmount;

    @Schema(description = "最高交易金额")
    private BigDecimal maxAmount;

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
