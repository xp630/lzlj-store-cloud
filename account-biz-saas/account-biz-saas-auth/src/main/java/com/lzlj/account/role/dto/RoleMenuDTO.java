package com.lzlj.account.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 角色菜单分配请求DTO
 */
@Data
@Schema(description = "角色菜单分配请求")
public class RoleMenuDTO {

    @Schema(description = "菜单ID列表")
    private List<Long> menuIds;
}
