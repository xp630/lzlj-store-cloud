package com.lzlj.account.biz.tenant.channel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 租户渠道实体
 */
@Data
@TableName("saas_auth_tenant_channel")
public class SaasTenantChannel extends BaseEntity {

    /**
     * 租户ID
     */
    private Long tenantId;

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

}
