package com.lzlj.account.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_merchant")
public class LzljMerchant extends BaseEntity {

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
     * 母商户ID（子户时必填）
     */
    private Long parentId;

    /**
     * 网商商户账号
     */
    private String wangshangAccount;

    /**
     * 业务场景代码列表（母户用，JSON数组）
     */
    private String scenarioCodes;

    /**
     * 业务场景ID（子户用）
     */
    private Long scenarioId;
}
