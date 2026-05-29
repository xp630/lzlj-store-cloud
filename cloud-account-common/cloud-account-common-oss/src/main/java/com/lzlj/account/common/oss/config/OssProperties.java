package com.lzlj.account.common.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * OSS 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    /**
     * 是否启用OSS
     */
    private boolean enabled = false;

    /**
     * OSS endpoint
     */
    private String endpoint;

    /**
     * Bucket名称
     */
    private String bucket;

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * CDN域名（可选，用于返回公网URL）
     */
    private String cdnDomain;

    /**
     * 签名URL过期时间（秒）
     */
    private Integer expireSeconds = 300;

    /**
     * 最大文件大小（字节），默认5MB
     */
    private Long maxFileSize = 5L * 1024 * 1024;

    public boolean isConfigured() {
        return enabled && endpoint != null && bucket != null
                && accessKeyId != null && accessKeySecret != null;
    }
}
