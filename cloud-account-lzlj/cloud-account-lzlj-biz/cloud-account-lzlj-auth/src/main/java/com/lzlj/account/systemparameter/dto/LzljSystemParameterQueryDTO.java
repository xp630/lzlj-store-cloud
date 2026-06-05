package com.lzlj.account.systemparameter.dto;

import com.lzlj.account.common.core.domain.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "LZLJ系统参数查询参数")
public class LzljSystemParameterQueryDTO extends PageRequest {

    @Schema(description = "参数名称（模糊搜索）")
    private String paramName;

    @Schema(description = "状态")
    private Integer status;
}
