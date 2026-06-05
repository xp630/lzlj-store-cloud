package com.lzlj.account.systemparameter.dto;

import com.lzlj.account.common.core.domain.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "LZLJ系统参数查询参数")
public class LzljSystemParameterQueryDTO extends PageRequest {
    private String paramName;
    private Integer status;
}
