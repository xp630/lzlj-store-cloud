package com.lzlj.account.datarole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据角色详情DTO
 */
@Data
@Schema(description = "数据角色详情")
public class DataRoleDTO {

    private Long id;

    @Schema(description = "数据角色名称")
    private String dataRoleName;

    @Schema(description = "数据角色编码")
    private String dataRoleCode;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "数据权限条件列表")
    private List<ConditionDTO> conditions;
}
