package com.lzlj.account.user.dto;

import com.lzlj.account.role.dto.LzljRoleDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LZLJ 用户DTO
 */
@Data
public class LzljUserDTO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
    private Integer status;
    private Integer userType;
    private Long orgId;
    private Long lastLoginTime;
    private LocalDateTime createTime;
    /** 用户角色列表 */
    private List<LzljRoleDTO> roles;
}
