package com.lzlj.account.datarole.dto;

import com.lzlj.account.common.core.domain.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据角色查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "数据角色查询")
public class DataRoleQueryDTO extends PageQueryDTO {

    @Schema(description = "数据角色名称")
    private String dataRoleName;

    @Schema(description = "状态")
    private Integer status;
}
