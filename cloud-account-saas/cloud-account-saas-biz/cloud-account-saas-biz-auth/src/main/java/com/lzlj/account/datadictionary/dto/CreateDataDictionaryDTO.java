package com.lzlj.account.datadictionary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建数据字典请求DTO
 */
@Data
@Schema(description = "创建数据字典请求")
public class CreateDataDictionaryDTO {

    @NotBlank(message = "字典编码不能为空")
    @Schema(description = "字典编码")
    private String dictCode;

    @NotBlank(message = "字典类型不能为空")
    @Schema(description = "字典类型")
    private String dictType;

    @NotBlank(message = "字典标签不能为空")
    @Schema(description = "字典标签")
    private String dictLabel;

    @NotBlank(message = "字典值不能为空")
    @Schema(description = "字典值")
    private String dictValue;

    @Schema(description = "排序")
    private Integer sort;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
