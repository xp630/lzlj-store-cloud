package com.lzlj.account.user.dto;

import com.lzlj.account.role.dto.RoleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 DTO
 */
@Data
@Schema(description = "用户信息")
public class UserDTO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "性别 0:未知 1:男 2:女")
    private Integer gender;

    @Schema(description = "状态 0:禁用 1:启用 2:锁定")
    private Integer status;

    @Schema(description = "用户类型 1:超级管理员 2:管理员 3:普通用户")
    private Integer userType;

    @Schema(description = "组织ID")
    private Long orgId;

    @Schema(description = "组织名称")
    private String orgName;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "最后登录IP")
    private String lastLoginIp;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人名称")
    private String createByName;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人名称")
    private String updateByName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "用户角色列表")
    private List<RoleDTO> roles;

    @Schema(description = "可管理的租户ID列表")
    private List<Long> tenantIds;
}
