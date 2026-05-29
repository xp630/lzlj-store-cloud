package com.lzlj.account.datadictionary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据字典查询DTO
 */
@Data
@Schema(description = "数据字典查询参数")
public class DataDictionaryQueryDTO {

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;
}
