package com.lzlj.account.gateway.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * API访问日志记录器
 * 使用 logback 写入本地文件，按天分片，自动清理过期日志
 */
@Slf4j
@Component
public class ApiAccessLogger {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 记录API访问日志
     * 日志输出到专用文件，由 logback 的 RollingFileAppender 处理滚动和清理
     */
    public void log(Long apiKeyId, String apiKey, Long tenantId, String method, String path,
                    String requestBody, String responseBody, Integer statusCode,
                    Long duration, String ip, String userAgent, String errorMsg) {
        ApiAccessLog accessLog = new ApiAccessLog();
        accessLog.setApiKeyId(apiKeyId);
        accessLog.setApiKey(apiKey);
        accessLog.setTenantId(tenantId);
        accessLog.setMethod(method);
        accessLog.setPath(path);
        accessLog.setRequestBody(requestBody);
        accessLog.setResponseBody(responseBody);
        accessLog.setStatusCode(statusCode);
        accessLog.setDuration(duration);
        accessLog.setIp(ip);
        accessLog.setUserAgent(userAgent);
        accessLog.setErrorMsg(errorMsg);

        try {
            // 使用专门的 logger 输出 JSON 格式日志
            log.info(objectMapper.writeValueAsString(accessLog));
        } catch (Exception e) {
            log.error("序列化API日志失败", e);
        }
    }
}
