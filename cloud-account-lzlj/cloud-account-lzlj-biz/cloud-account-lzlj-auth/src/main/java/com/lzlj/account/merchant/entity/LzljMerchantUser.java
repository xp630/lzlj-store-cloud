package com.lzlj.account.merchant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * LZLJ 商户账号关联实体
 */
@Data
@TableName("lzlj_auth_merchant_user")
public class LzljMerchantUser {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色：merchant_admin门店管理员/clerk店员/cashier收银员
     */
    private String role;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 删除标记 0:未删除 1:已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
