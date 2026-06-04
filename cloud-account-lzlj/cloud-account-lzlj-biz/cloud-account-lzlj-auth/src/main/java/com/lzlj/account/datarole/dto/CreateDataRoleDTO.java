package com.lzlj.account.datarole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 创建数据角色请求
 */
@Data
@Schema(description = "创建数据角色请求")
public class CreateDataRoleDTO {

    @NotBlank(message = "数据角色名称不能为空")
    @Schema(description = "数据角色名称")
    private String dataRoleName;

    @NotBlank(message = "数据角色编码不能为空")
    @Schema(description = "数据角色编码")
    private String dataRoleCode;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;

    @Schema(description = "数据权限条件列表")
    private List<ConditionDTO> conditions;
}
