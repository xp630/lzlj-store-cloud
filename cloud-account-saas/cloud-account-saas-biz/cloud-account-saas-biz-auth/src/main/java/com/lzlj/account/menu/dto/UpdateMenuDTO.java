package com.lzlj.account.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 更新菜单请求DTO
 */
@Data
@Schema(description = "更新菜单请求")
public class UpdateMenuDTO {

    @NotBlank(message = "菜单名称不能为空")
    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "父菜单ID（顶级为0）")
    private Long parentId;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @NotNull(message = "菜单类型不能为空")
    @Schema(description = "类型（0:目录 1:菜单 2:按钮）")
    private Integer type;

    @Schema(description = "权限标识（如: system:user:list）")
    private String permission;

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;
}
