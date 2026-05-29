package com.lzlj.account.datadictionary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据字典响应DTO
 */
@Data
@Schema(description = "数据字典响应")
public class DataDictionaryDTO {

    private Long id;

    /**
     * 字典编码
     */
    private String dictCode;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
