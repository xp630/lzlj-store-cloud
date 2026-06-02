package com.lzlj.account.common.core.domain.paymentchannel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 统一支付通道DTO
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

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;

    @Schema(description = "云账户管理费率")
    private BigDecimal cloudAccountFee;

    @Schema(description = "上游成本费率")
    private BigDecimal upstreamCostFee;

    @Schema(description = "总费率成本（技术服务费）")
    private BigDecimal totalFeeCost;

    @Schema(description = "单笔限额")
    private BigDecimal perTransactionLimit;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
