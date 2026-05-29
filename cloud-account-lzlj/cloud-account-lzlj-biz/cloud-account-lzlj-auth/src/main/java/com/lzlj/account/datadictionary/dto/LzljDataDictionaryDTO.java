package com.lzlj.account.datadictionary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "LZLJ数据字典响应")
public class LzljDataDictionaryDTO {
    private Long id;
    private String dictCode;
    private String dictType;
    private String dictLabel;
    private String dictValue;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}
