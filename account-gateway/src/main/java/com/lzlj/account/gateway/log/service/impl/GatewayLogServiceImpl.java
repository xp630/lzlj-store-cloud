package com.lzlj.account.gateway.log.service.impl;

import com.lzlj.account.gateway.config.AuthServiceUrlProvider;
import com.lzlj.account.gateway.log.service.GatewayLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 网关日志服务实现
 * 通过WebClient调用auth服务记录API访问日志
 */
@Slf4j
@Service
public class GatewayLogServiceImpl implements GatewayLogService {

    private final WebClient.Builder webClientBuilder;
    private final AuthServiceUrlProvider authServiceUrlProvider;

    public GatewayLogServiceImpl(WebClient.Builder webClientBuilder, AuthServiceUrlProvider authServiceUrlProvider) {
        this.webClientBuilder = webClientBuilder;
        this.authServiceUrlProvider = authServiceUrlProvider;
    }

    @Async
    @Override
    public void logApiAccessAsync(Long apiKeyId, String apiKey, Long tenantId, String method,
                                  String path, String requestBody, String responseBody,
                                  Integer statusCode, Long duration, String ip, String userAgent, String errorMsg) {
        try {
            String authServiceUrl = authServiceUrlProvider.getAuthServiceUrl();
            if (authServiceUrl == null) {
                log.warn("无法获取auth服务地址，跳过日志记录");
                return;
            }

            // 构建请求体
            String requestBodyJson = String.format(
                    "{\"apiKeyId\":%d,\"apiKey\":\"%s\",\"tenantId\":%d,\"method\":\"%s\",\"path\":\"%s\",\"requestBody\":\"%s\",\"responseBody\":\"%s\",\"statusCode\":%d,\"duration\":%d,\"ip\":\"%s\",\"userAgent\":\"%s\",\"errorMsg\":\"%s\"}",
                    apiKeyId != null ? apiKeyId : 0,
                    apiKey != null ? apiKey : "",
                    tenantId != null ? tenantId : 0,
                    method != null ? method : "",
                    path != null ? path : "",
                    escapeJson(requestBody),
                    escapeJson(responseBody),
                    statusCode != null ? statusCode : 0,
                    duration != null ? duration : 0,
                    escapeJson(ip),
                    escapeJson(userAgent),
                    escapeJson(errorMsg)
            );

            // 调用auth服务的日志接口
            webClientBuilder.build()
                    .post()
                    .uri(authServiceUrl + "/inner/log/api")
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBodyJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(
                            response -> log.debug("API日志记录成功: apiKey={}, path={}", apiKey, path),
                            error -> log.error("API日志记录失败: apiKey={}, path={}, error={}", apiKey, path, error.getMessage())
                    );
        } catch (Exception e) {
            log.error("调用日志服务失败: apiKey={}, path={}", apiKey, path, e);
        }
    }

    /**
     * JSON字符串转义
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
