package com.lzlj.account.datadictionary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "LZLJ数据字典查询参数")
public class LzljDataDictionaryQueryDTO {
    private String dictType;
    private Integer status;
}
