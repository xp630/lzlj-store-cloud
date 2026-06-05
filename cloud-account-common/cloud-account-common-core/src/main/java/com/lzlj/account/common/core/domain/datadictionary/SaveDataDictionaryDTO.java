package com.lzlj.account.common.core.domain.datadictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 保存（创建/更新）数据字典请求
 */
@Data
@Schema(description = "保存（创建/更新）数据字典请求")
public class SaveDataDictionaryDTO {

    @Schema(description = "ID（为空则新增，不为空则更新）")
    private Long id;

    @NotBlank(message = "字典编码不能为空")
    private String dictCode;

    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;

    @NotBlank(message = "字典值不能为空")
    private String dictValue;

    private Integer sort;

    @NotNull(message = "状态不能为空")
    private Integer status;

    private String remark;
}
