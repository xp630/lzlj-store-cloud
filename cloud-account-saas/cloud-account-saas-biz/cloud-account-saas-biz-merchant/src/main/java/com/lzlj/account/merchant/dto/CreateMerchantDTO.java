package com.lzlj.account.merchant.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 创建商户DTO
 */
@Data
public class CreateMerchantDTO {

    /**
     * 商户编码
     */
    @NotBlank(message = "商户编码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "商户编码只能包含字母、数字和下划线")
    private String merchantCode;

    /**
     * 商户名称
     */
    @NotBlank(message = "商户名称不能为空")
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
     * 备注
     */
    private String remark;
}
