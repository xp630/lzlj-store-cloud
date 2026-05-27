package com.lzlj.account.openapi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * API密钥实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("saas_auth_api_key")
public class ApiKey extends BaseEntity {

    /**
     * 绑定的租户ID
     */
    private Long tenantId;

    /**
     * API公钥（ak_开头）
     */
    private String apiKey;

    /**
     * API密钥（加密存储）
     */
    private String apiSecret;

    /**
     * API名称
     */
    private String name;

    /**
     * API描述
     */
    private String description;

    /**
     * 状态（0:禁用 1:启用）
     */
    private Integer status;

    /**
     * 速率限制（次/分钟）
     */
    private Integer rateLimit;

    /**
     * 过期时间（NULL表示永不过期）
     */
    private LocalDateTime expiresTime;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedTime;
}
