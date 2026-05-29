package com.lzlj.account.systemparameter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 创建系统参数请求DTO
 */
@Data
@Schema(description = "创建系统参数请求")
public class CreateSystemParameterDTO {

    @NotBlank(message = "参数编码不能为空")
    @Schema(description = "参数编码")
    private String paramKey;

    @NotBlank(message = "参数名称不能为空")
    @Schema(description = "参数名称")
    private String paramName;

    @NotBlank(message = "参数值不能为空")
    @Schema(description = "参数值")
    private String paramValue;

    @NotBlank(message = "参数类型不能为空")
    @Pattern(regexp = "^(STRING|INTEGER|BOOLEAN|DECIMAL)$", message = "参数类型只能是 STRING、INTEGER、BOOLEAN 或 DECIMAL")
    @Schema(description = "参数类型（STRING/INTEGER/BOOLEAN/DECIMAL）")
    private String paramType;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
