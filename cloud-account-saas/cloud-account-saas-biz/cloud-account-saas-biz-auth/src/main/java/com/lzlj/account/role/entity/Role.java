package com.lzlj.account.role.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体（平台级数据，无租户隔离）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_role")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码（如: ADMIN）
     */
    private String roleCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;
}
