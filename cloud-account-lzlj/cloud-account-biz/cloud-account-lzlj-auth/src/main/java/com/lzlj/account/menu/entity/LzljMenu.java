package com.lzlj.account.menu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 菜单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_menu")
public class LzljMenu extends BaseEntity {

    /**
     * 父菜单ID
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单路径
     */
    private String path;

    /**
     * 菜单组件路径
     */
    private String component;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 菜单类型 0:目录 1:菜单 2:按钮
     */
    private Integer type;

    /**
     * 权限标识
     */
    private String permission;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 组织ID
     */
    private Long orgId;
}
