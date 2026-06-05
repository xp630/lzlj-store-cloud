package com.lzlj.account.biz.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改密码请求
 */
@Data
@Schema(description = "修改密码请求")
public class ChangePasswordDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码", example = "123456")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码", example = "654321")
    private String newPassword;
}
