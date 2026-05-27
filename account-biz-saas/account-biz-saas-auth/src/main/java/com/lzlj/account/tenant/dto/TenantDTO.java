package com.lzlj.account.tenant.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户DTO
 */
@Data
public class TenantDTO {

    private Long id;

    /**
     * 租户编码
     */
    private String tenantCode;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户描述
     */
    private String tenantDesc;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 套餐ID
     */
    private Long packageId;

    /**
     * 用户数量上限
     */
    private Integer userLimit;

    /**
     * logo
     */
    private String logo;

    private LocalDateTime createTime;
}
