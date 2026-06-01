package com.lzlj.account.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 商户银联账户信息实体（各支付渠道收款账户）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_merchant_channel_account")
public class LzljMerchantChannelAccount extends BaseEntity {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 支付渠道ID
     */
    private Long channelId;

    /**
     * 银联账号
     */
    private String unionPayAccount;

    /**
     * 开户名称
     */
    private String accountName;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;
}
