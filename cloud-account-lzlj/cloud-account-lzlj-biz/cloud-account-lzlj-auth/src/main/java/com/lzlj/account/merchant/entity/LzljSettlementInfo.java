package com.lzlj.account.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 结算信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_merchant_settlement_info")
public class LzljSettlementInfo extends BaseEntity {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 结算类型 1:对公 2:对私
     */
    private Integer settlementType;

    /**
     * 开户行名称
     */
    private String bankName;

    /**
     * 开户行支行
     */
    private String bankBranchName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 开户名称
     */
    private String accountName;

    /**
     * 结算周期 T+N
     */
    private String settlementCycle;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;
}
