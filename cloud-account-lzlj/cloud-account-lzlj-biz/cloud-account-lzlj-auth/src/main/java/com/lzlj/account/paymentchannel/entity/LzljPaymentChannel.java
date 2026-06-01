package com.lzlj.account.paymentchannel.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * LZLJ 支付通道实体（平台级数据，无租户隔离）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_payment_channel")
public class LzljPaymentChannel extends BaseEntity {

    /**
     * 通道编码，如 WECHAT/ALIPAY/NETBANK
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 通道类型，如 WECHAT/ALIPAY/UNIONPAY/NETBANK
     */
    private String channelType;

    /**
     * 支付方式，如 WECHAT/ALIPAY/FASTPAY
     */
    private String paymentMethod;

    /**
     * 描述
     */
    private String description;

    /**
     * 费率
     */
    private BigDecimal feeRate;

    /**
     * 最低交易金额
     */
    private BigDecimal minAmount;

    /**
     * 最高交易金额
     */
    private BigDecimal maxAmount;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;
}
