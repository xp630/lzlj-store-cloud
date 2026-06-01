package com.lzlj.account.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import com.lzlj.account.common.core.enums.MerchantAccountStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_merchant")
public class LzljMerchant extends  BaseEntity{
    /**
     * 商户编号 M-001
     */
    private String merchantCode;

    /**
     * 商户全称
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
     * 省代码
     */
    private String provinceCode;

    /**
     * 市代码
     */
    private String cityCode;

    /**
     * 区代码
     */
    private String districtCode;

    /**
     * 详细地址
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
     * 开户状态 0:未开户 1:开户中 2:已开户 3:开户失败
     */
    private Integer accountStatus;

    /**
     * 商户类型 1:母户 2:子户
     */
    private Integer merchantType;

    /**
     * 网商商户账号
     */
    private String wangshangAccount;
}
