package com.lzlj.account.systemparameter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统参数查询DTO
 */
@Data
@Schema(description = "系统参数查询参数")
public class SystemParameterQueryDTO {

    @Schema(description = "参数名称（模糊搜索）")
    private String paramName;

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;
}
