package com.lzlj.account.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单DTO
 */
@Data
@Schema(description = "菜单响应")
public class MenuDTO {

    private Long id;

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
     * 权限标识
     */
    private String permission;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;

    /**
     * 是否已授权（用于权限分配）
     */
    private Boolean checked;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 子菜单
     */
    private List<MenuDTO> children;
}
