package com.lzlj.account.biz.paychannel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付通道实体
 */
@Data
@TableName("saas_auth_payment_channel")
public class SaasPaymentChannel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 通道编码（UNIONPAY/银联, NETBANK/网商）
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

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createBy;

    private Long updateBy;

    @TableLogic
    private Integer deleted;

    private String remark;

    @Version
    private Integer version;
}
