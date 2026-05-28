package com.lzlj.account.merchant.dto;

import lombok.Data;

/**
 * 更新商户DTO
 */
@Data
public class UpdateMerchantDTO {

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户简称
     */
    private String shortName;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 营业执照号
     */
    private String licenseNo;

    /**
     * 法人代表
     */
    private String legalPerson;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
