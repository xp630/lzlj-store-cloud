package com.lzlj.account.paymentchannel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付通道DTO
 */
@Data
@Schema(description = "支付通道响应")
public class PaymentChannelDTO {

    private Long id;

    /**
     * 通道编码（UNIONPAY/NETBANK）
     */
    private String channelCode;

    /**
     * 通道名称（银联/网商）
     */
    private String channelName;

    /**
     * 支付方式（逗号分隔，如 WECHAT,ALIPAY）
     */
    private String paymentMethod;

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

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
