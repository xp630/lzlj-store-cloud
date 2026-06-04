package com.lzlj.account.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建LZLJ用户请求DTO
 */
@Data
@Schema(description = "创建LZLJ用户请求")
public class CreateLzljUserDTO {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别 0:未知 1:男 2:女")
    private Integer gender;

    @NotNull(message = "用户类型不能为空")
    @Schema(description = "用户类型 1:超级管理员 2:管理员 3:普通用户")
    private Integer userType;

    @NotNull(message = "组织ID不能为空")
    @Schema(description = "组织ID")
    private Long orgId;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status = 1;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}
