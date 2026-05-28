package com.lzlj.account.role.dto;

import lombok.Data;

/**
 * LZLJ 更新角色DTO
 */
@Data
public class LzljUpdateRoleDTO {
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
}
