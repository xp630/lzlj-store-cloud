package com.lzlj.account.systemparameter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "更新LZLJ系统参数请求")
public class UpdateLzljSystemParameterDTO {

    @NotBlank(message = "参数编码不能为空")
    private String paramKey;

    @NotBlank(message = "参数名称不能为空")
    private String paramName;

    @NotBlank(message = "参数值不能为空")
    private String paramValue;

    @NotBlank(message = "参数类型不能为空")
    @Pattern(regexp = "^(STRING|INTEGER|BOOLEAN|DECIMAL)$", message = "参数类型只能是 STRING、INTEGER、BOOLEAN 或 DECIMAL")
    private String paramType;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String remark;
}
