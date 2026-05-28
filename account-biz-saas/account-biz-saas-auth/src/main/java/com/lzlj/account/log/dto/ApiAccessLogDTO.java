package com.lzlj.account.log.dto;

import lombok.Data;

/**
 * API访问日志请求DTO
 */
@Data
public class ApiAccessLogDTO {
    private Long apiKeyId;
    private String apiKey;
    private Long tenantId;
    private String method;
    private String path;
    private String requestBody;
    private String responseBody;
    private Integer statusCode;
    private Long duration;
    private String ip;
    private String userAgent;
    private String errorMsg;
}
