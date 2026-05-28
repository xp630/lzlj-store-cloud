package com.lzlj.account.role.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 更新角色请求DTO
 */
@Data
@Schema(description = "更新角色请求")
public class UpdateRoleDTO {

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;
}
