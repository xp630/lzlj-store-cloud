package com.lzlj.account.openapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API密钥响应DTO
 */
@Data
@Schema(description = "API密钥响应")
public class ApiKeyDTO {

    private Long id;

    /**
     * 绑定的租户ID
     */
    private Long tenantId;

    /**
     * API公钥
     */
    private String apiKey;

    /**
     * API密钥（创建时返回，后续不再显示）
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
     * 状态
     */
    private Integer status;

    /**
     * 速率限制
     */
    private Integer rateLimit;

    /**
     * 过期时间
     */
    private LocalDateTime expiresTime;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 密钥是否已保存提示（创建后设为false）
     */
    private Boolean secretSaved;
}
