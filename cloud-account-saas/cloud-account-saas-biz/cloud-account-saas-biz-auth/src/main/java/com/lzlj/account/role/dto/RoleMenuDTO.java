package com.lzlj.account.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 角色菜单分配请求DTO
 */
@Data
@Schema(description = "角色菜单分配请求（全量替换）")
public class RoleMenuDTO {

    @Schema(description = "菜单ID列表（全量替换：传入的ID列表将替换角色现有的所有菜单权限）", example = "[1, 2, 3]")
    private List<Long> menuIds;
}
