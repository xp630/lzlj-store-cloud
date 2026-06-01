package com.lzlj.account.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户法人信息实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_merchant_legal")
public class MerchantLegal extends TenantEntity {

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 营业执照号
     */
    private String licenseNo;

    /**
     * 法人代表
     */
    private String legalPerson;

    /**
     * 营业执照图片URL
     */
    private String licensePic;

    /**
     * 法人身份证号
     */
    private String legalIdCard;

    /**
     * 法人身份证图片URL
     */
    private String legalIdCardPic;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;
}
