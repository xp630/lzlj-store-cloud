package com.lzlj.account.gateway.log.service;

/**
 * 网关日志服务接口
 */
public interface GatewayLogService {

    /**
     * 异步记录API访问日志
     */
    void logApiAccessAsync(Long apiKeyId, String apiKey, Long tenantId, String method,
                          String path, String requestBody, String responseBody,
                          Integer statusCode, Long duration, String ip, String userAgent, String errorMsg);
}
