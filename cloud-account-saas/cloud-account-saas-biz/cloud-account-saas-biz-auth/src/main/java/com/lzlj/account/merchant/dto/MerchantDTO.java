package com.lzlj.account.merchant.dto;

import com.lzlj.account.common.core.domain.merchant.MerchantBaseDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantChannelAccountDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantLegalDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * SaaS 商户DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantDTO extends MerchantBaseDTO {

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 组织ID
     */
    private Long orgId;

    /**
     * 支付渠道账号
     */
    private String wangshangAccount;

    /**
     * 法人信息
     */
    private MerchantLegalDTO legal;

    /**
     * 银联账户列表
     */
    private List<MerchantChannelAccountDTO> channelAccounts;
}
