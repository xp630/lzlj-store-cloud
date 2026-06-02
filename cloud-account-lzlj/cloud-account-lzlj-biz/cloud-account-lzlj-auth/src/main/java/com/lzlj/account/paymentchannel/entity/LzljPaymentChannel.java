package com.lzlj.account.paymentchannel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * LZLJ 支付通道实体（平台级数据，无租户隔离）
 * 字段与 SaaS PaymentChannel 保持一致
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_payment_channel")
public class LzljPaymentChannel extends BaseEntity {

    /**
     * 通道编码，如 UNIONPAY/NETBANK
     */
    private String channelCode;

    /**
     * 通道名称，如 银联/网商
     */
    private String channelName;

    /**
     * 支付方式，如 WECHAT,ALIPAY,BANK_CARD
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
     * 状态 0:禁用 1:启用
     */
    private Integer status;
}
