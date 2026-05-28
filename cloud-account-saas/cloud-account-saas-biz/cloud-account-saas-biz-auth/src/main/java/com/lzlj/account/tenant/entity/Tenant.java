package com.lzlj.account.tenant.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 租户实体
 * 注意：租户表本身不隔离（不继承TenantEntity），避免自动添加tenant_id条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_tenant")
public class Tenant extends BaseEntity {

    /**
     * 租户编码（唯一）
     */
    private String tenantCode;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户描述
     */
    private String tenantDesc;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

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
     * 套餐ID
     */
    private Long packageId;

    /**
     * 用户数量上限
     */
    private Integer userLimit;

    /**
     * logo
     */
    private String logo;
}
