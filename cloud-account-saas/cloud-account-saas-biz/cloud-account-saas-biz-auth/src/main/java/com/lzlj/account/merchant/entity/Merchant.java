package com.lzlj.account.merchant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_merchant")
public class Merchant extends TenantEntity {

    /**
     * 商户编码（唯一）
     */
    private String merchantCode;

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
     * 支付渠道账号
     */
    private String wangshangAccount;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;
}
