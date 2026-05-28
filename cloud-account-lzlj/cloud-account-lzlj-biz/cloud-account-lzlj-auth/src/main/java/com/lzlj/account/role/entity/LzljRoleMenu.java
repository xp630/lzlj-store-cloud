package com.lzlj.account.role.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 角色菜单关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_role_menu")
public class LzljRoleMenu extends BaseEntity {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;

    /**
     * 组织ID
     */
    private Long orgId;
}
