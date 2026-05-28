package com.lzlj.account.role.dto;

import lombok.Data;

/**
 * LZLJ 角色DTO
 */
@Data
public class LzljRoleDTO {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private Integer status;
    private Long orgId;
}
