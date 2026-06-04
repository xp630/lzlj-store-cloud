package com.lzlj.account.tenant.entity;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员租户关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_admin_tenant")
@InterceptorIgnore(tenantLine = "true")
public class AdminTenant extends BaseEntity {

    /**
     * 管理员用户ID
     */
    @TableField("user_id")
    private Long adminUserId;

    /**
     * 可管理的租户ID
     */
    private Long tenantId;
}
