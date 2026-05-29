package com.lzlj.account.systemparameter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "LZLJ系统参数查询参数")
public class LzljSystemParameterQueryDTO {
    private String paramName;
    private Integer status;
}
