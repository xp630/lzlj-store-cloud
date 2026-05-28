package com.lzlj.account.gateway.filter;

import lombok.Data;

/**
 * API密钥认证信息
 */
@Data
public class ApiKeyAuthInfo {
    private Long id;
    private String apiKey;
    private String apiSecret;
    private Long tenantId;
    private Integer status;
}
