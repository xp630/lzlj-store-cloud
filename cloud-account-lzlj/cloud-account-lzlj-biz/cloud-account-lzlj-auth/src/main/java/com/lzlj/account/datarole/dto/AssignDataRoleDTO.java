package com.lzlj.account.datarole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 分配数据角色请求
 */
@Data
@Schema(description = "分配数据角色请求")
public class AssignDataRoleDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;

    @NotNull(message = "数据角色ID列表不能为空")
    @Schema(description = "数据角色ID列表")
    private List<Long> dataRoleIds;
}
