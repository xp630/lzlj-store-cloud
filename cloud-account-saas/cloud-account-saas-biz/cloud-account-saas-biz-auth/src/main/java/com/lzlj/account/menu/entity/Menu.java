package com.lzlj.account.menu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单实体（平台级数据，无租户隔离）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_menu")
public class Menu extends BaseEntity {

    /**
     * 父菜单ID（顶级为0）
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 类型（0:目录 1:菜单 2:按钮）
     */
    private Integer type;

    /**
     * 权限标识（如: system:user:list）
     */
    private String permission;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;
}
