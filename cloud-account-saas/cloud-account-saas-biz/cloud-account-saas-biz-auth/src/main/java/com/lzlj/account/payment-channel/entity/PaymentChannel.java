package com.lzlj.account.paymentchannel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 支付通道实体（平台级数据，无租户隔离）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_payment_channel")
public class PaymentChannel extends BaseEntity {

    /**
     * 主键ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 通道编码（UNIONPAY/银联, NETBANK/网商）
     */
    private String channelCode;

    /**
     * 通道名称
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
}
