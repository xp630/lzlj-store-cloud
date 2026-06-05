package com.lzlj.account.biz.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 更新用户请求DTO
 */
@Data
@Schema(description = "更新用户请求")
public class UpdateUserDTO {

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "性别 0:未知 1:男 2:女", example = "1")
    private Integer gender;

    @Schema(description = "用户类型 1:超级管理员 2:管理员 3:普通用户", example = "2")
    private Integer userType;

    @Schema(description = "组织ID", example = "1")
    private Long orgId;

    @Schema(description = "状态 0:禁用 1:启用", example = "1")
    private Integer status;

    @Schema(description = "角色ID列表（全量替换：传入的角色ID列表将替换用户当前所有角色）", example = "[1, 2]")
    private List<Long> roleIds;

    @Schema(description = "可管理的租户ID列表（全量替换：传入的租户ID列表将替换用户当前可管理的所有租户）", example = "[1, 2, 3]")
    private List<Long> tenantIds;

    @Schema(description = "备注")
    private String remark;
}
