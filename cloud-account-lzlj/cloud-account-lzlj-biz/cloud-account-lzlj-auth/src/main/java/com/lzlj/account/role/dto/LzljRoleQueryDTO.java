package com.lzlj.account.role.dto;

import com.lzlj.account.common.core.domain.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ角色查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "LZLJ角色查询条件")
public class LzljRoleQueryDTO extends PageRequest {

    @Schema(description = "角色名称（模糊搜索）")
    private String keyword;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
}
