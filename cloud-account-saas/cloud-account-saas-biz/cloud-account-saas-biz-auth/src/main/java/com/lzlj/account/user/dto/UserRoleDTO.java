package com.lzlj.account.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户角色分配请求DTO
 */
@Data
@Schema(description = "用户角色分配请求（全量替换）")
public class UserRoleDTO {

    @Schema(description = "角色ID列表（全量替换：传入的ID列表将替换用户现有的所有角色）", example = "[1, 2]")
    private List<Long> roleIds;
}
