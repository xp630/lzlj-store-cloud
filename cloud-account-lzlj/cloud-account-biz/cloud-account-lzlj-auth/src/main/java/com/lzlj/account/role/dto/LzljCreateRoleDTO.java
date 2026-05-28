package com.lzlj.account.role.dto;

import lombok.Data;

/**
 * LZLJ 创建角色DTO
 */
@Data
public class LzljCreateRoleDTO {
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private Long orgId;
}
