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
    /** 最后登录IP */
    private String lastLoginIp;
    /** 最后登录时间 */
    private Long lastLoginTime;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 创建人名称 */
    private String createByName;
    /** 更新时间 */
    private LocalDateTime updateTime;
    /** 更新人名称 */
    private String updateByName;
    /** 用户角色列表 */
    private List<LzljRoleDTO> roles;
}
