package com.lzlj.account.biz.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 修改用户状态请求
 */
@Data
@Schema(description = "修改用户状态请求")
public class ChangeStatusDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态 0:禁用 1:启用", example = "1")
    private Integer status;
}
