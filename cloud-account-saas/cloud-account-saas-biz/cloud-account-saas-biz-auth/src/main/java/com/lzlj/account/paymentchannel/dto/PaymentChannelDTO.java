package com.lzlj.account.paymentchannel.dto;

import com.lzlj.account.common.core.domain.paymentchannel.PaymentChannelDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * SaaS 支付通道DTO（扩展统一DTO）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "支付通道响应")
public class PaymentChannelDTO extends com.lzlj.account.common.core.domain.paymentchannel.PaymentChannelDTO {

    /**
     * 云账户管理费率
     */
    private BigDecimal cloudAccountFee;

    /**
     * 上游成本费率
     */
    private BigDecimal upstreamCostFee;

    /**
     * 总费率成本（技术服务费）
     */
    private BigDecimal totalFeeCost;

    /**
     * 单笔限额
     */
    private BigDecimal perTransactionLimit;
}
