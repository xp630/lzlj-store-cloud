package com.lzlj.account.biz.merchantchannel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户渠道实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_merchant_channel")
public class SaasMerchantChannel extends TenantEntity {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 渠道ID
     */
    private Long channelId;

    /**
     * 开通状态(0关闭 1开通)
     */
    private Integer status;

    /**
     * 费率类型(1固定费率)
     */
    private Integer rateType;

    /**
     * 费率值(如0.006表示0.6%)
     */
    private BigDecimal rateValue;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
