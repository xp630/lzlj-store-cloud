package com.lzlj.account.user.dto;

import lombok.Data;

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
}
