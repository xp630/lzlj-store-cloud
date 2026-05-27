package com.lzlj.account.log.service;

/**
 * 日志服务接口
 */
public interface LogService {

    /**
     * 记录操作日志
     *
     * @param userId    用户ID
     * @param tenantId  租户ID
     * @param username  用户名
     * @param module    模块
     * @param operation 操作类型
     * @param content   操作内容
     * @param bizId     业务ID
     * @param ip        IP地址
     * @param userAgent 用户UA
     */
    void logOperation(Long userId, Long tenantId, String username, String module,
                     String operation, String content, Long bizId, String ip, String userAgent);

    /**
     * 记录API访问日志
     *
     * @param apiKeyId     API密钥ID
     * @param apiKey       API公钥
     * @param tenantId     租户ID
     * @param method       HTTP方法
     * @param path         请求路径
     * @param requestBody  请求体
     * @param responseBody 响应体
     * @param statusCode   HTTP状态码
     * @param duration     耗时（毫秒）
     * @param ip           客户端IP
     * @param userAgent    客户端UA
     * @param errorMsg     错误信息
     */
    void logApiAccess(Long apiKeyId, String apiKey, Long tenantId, String method,
                      String path, String requestBody, String responseBody,
                      Integer statusCode, Long duration, String ip, String userAgent, String errorMsg);
}
