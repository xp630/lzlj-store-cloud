package com.lzlj.account.role.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_role")
public class LzljRole extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 组织ID
     */
    private Long orgId;
}
