package com.lzlj.account.gateway.filter;

import com.lzlj.account.gateway.log.service.GatewayLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * OpenAPI 认证过滤器
 * 使用Spring Cloud DiscoveryClient解析服务地址，无需硬编码URL
 */
@Slf4j
@Component
public class OpenApiAuthFilter implements GlobalFilter, Ordered {

    private final DiscoveryClient discoveryClient;
    private final WebClient.Builder webClientBuilder;
    private final GatewayLogService gatewayLogService;

    @Value("${openapi.signature.expire-seconds:300}")
    private int signatureExpireSeconds;

    private static final String OPENAPI_PATH_PREFIX = "/openapi/";
    private static final String AUTH_SERVICE_ID = "saas-auth";

    public OpenApiAuthFilter(DiscoveryClient discoveryClient, WebClient.Builder webClientBuilder,
                            GatewayLogService gatewayLogService) {
        this.discoveryClient = discoveryClient;
        this.webClientBuilder = webClientBuilder;
        this.gatewayLogService = gatewayLogService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        String ip = getClientIp(request);
        String userAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);

        // 记录开始时间
        long startTime = System.currentTimeMillis();

        // 1. 检查是否是 OpenAPI 请求
        if (!path.startsWith(OPENAPI_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        // 2. 提取签名参数
        String apiKey = request.getHeaders().getFirst("X-API-Key");
        String timestampStr = request.getHeaders().getFirst("X-Timestamp");
        String signature = request.getHeaders().getFirst("X-Signature");

        // 3. 验证参数完整性
        if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(timestampStr) || !StringUtils.hasText(signature)) {
            logApiAccess(apiKey, 0L, method, path, null, null, 401, System.currentTimeMillis() - startTime, ip, userAgent, "缺少签名参数");
            return unauthorized(exchange, "缺少签名参数");
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            logApiAccess(apiKey, 0L, method, path, null, null, 401, System.currentTimeMillis() - startTime, ip, userAgent, "无效的时间戳格式");
            return unauthorized(exchange, "无效的时间戳格式");
        }

        // 4. 通过服务发现获取 auth 服务地址
        List<ServiceInstance> instances = discoveryClient.getInstances(AUTH_SERVICE_ID);
        if (instances == null || instances.isEmpty()) {
            log.error("未找到 auth 服务实例: {}", AUTH_SERVICE_ID);
            logApiAccess(apiKey, 0L, method, path, null, null, 503, System.currentTimeMillis() - startTime, ip, userAgent, "认证服务不可用");
            return unauthorized(exchange, "认证服务不可用");
        }

        ServiceInstance authInstance = instances.get(0);
        String authServiceUrl = "http://" + authInstance.getHost() + ":" + authInstance.getPort();
        log.info("Auth service URL: {}, host: {}, port: {}", authServiceUrl, authInstance.getHost(), authInstance.getPort());

        // 5. 调用 auth 服务查询认证信息
        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/openapi/key/inner/auth/" + apiKey)
                .retrieve()
                .bodyToMono(ApiKeyAuthInfo.class)
                .flatMap(authInfo -> {
                    if (authInfo == null) {
                        log.warn("API Key 不存在: apiKey={}", apiKey);
                        logApiAccess(apiKey, 0L, method, path, null, null, 401, System.currentTimeMillis() - startTime, ip, userAgent, "API Key 无效");
                        return unauthorized(exchange, "API Key 无效");
                    }

                    // 6. 解密 secret
                    String secret;
                    try {
                        secret = new String(Base64.getDecoder().decode(authInfo.getApiSecret()));
                    } catch (Exception e) {
                        log.error("解密 secret 失败: apiKey={}", apiKey);
                        logApiAccess(apiKey, authInfo.getTenantId(), method, path, null, null, 401, System.currentTimeMillis() - startTime, ip, userAgent, "API Key 无效");
                        return unauthorized(exchange, "API Key 无效");
                    }

                    // 7. 验签
                    boolean verified = SignatureUtils.verify(
                            timestamp, method, path, null, secret, signature, signatureExpireSeconds
                    );

                    if (!verified) {
                        log.warn("OpenAPI 签名验证失败: apiKey={}, path={}", apiKey, path);
                        logApiAccess(apiKey, authInfo.getTenantId(), method, path, null, null, 401, System.currentTimeMillis() - startTime, ip, userAgent, "签名验证失败");
                        return unauthorized(exchange, "签名验证失败");
                    }

                    log.debug("OpenAPI 认证成功: apiKey={}, tenantId={}, path={}", apiKey, authInfo.getTenantId(), path);

                    // 8. 验签通过，直接转发请求到后端服务（剥离 /openapi 前缀，但保留 /）
                    String backendPath = path.substring(OPENAPI_PATH_PREFIX.length() - 1);
                    return forwardToBackend(exchange, authInfo, authServiceUrl, backendPath, method, ip, userAgent, startTime);
                })
                .onErrorResume(e -> {
                    log.error("调用认证服务失败: apiKey={}, error={}", apiKey, e.getMessage(), e);
                    logApiAccess(apiKey, 0L, method, path, null, null, 500, System.currentTimeMillis() - startTime, ip, userAgent, "认证服务调用失败: " + e.getMessage());
                    return unauthorized(exchange, "API Key 无效");
                });
    }

    /**
     * 直接转发请求到后端服务
     */
    private Mono<Void> forwardToBackend(ServerWebExchange exchange, ApiKeyAuthInfo authInfo,
                                         String authServiceUrl, String originalPath, String method,
                                         String ip, String userAgent, long startTime) {
        log.info("forwardToBackend: authServiceUrl={}, originalPath={}", authServiceUrl, originalPath);
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        // 设置用户信息
        headers.set("X-User-Id", "0");
        headers.set("X-Tenant-Id", String.valueOf(authInfo.getTenantId()));
        headers.set("X-Username", "openapi");
        headers.set("X-API-Key", authInfo.getApiKey());

        // 移除转发相关的 headers
        headers.remove(HttpHeaders.HOST);
        headers.remove("X-Forwarded-For");
        headers.remove("X-Forwarded-Host");
        headers.remove("X-Forwarded-Port");
        headers.remove("X-Forwarded-Proto");

        return webClientBuilder.build()
                .method(exchange.getRequest().getMethod())
                .uri(authServiceUrl + originalPath)
                .headers(h -> h.putAll(headers))
                .retrieve()
                .bodyToMono(byte[].class)
                .flatMap(body -> {
                    response.getHeaders().setContentLength((long) body.length);
                    DataBuffer buffer = response.bufferFactory().wrap(body);
                    // 记录成功访问日志
                    String responseBody = new String(body, StandardCharsets.UTF_8);
                    logApiAccess(authInfo.getApiKey(), authInfo.getTenantId(), method, originalPath, null, responseBody, 200, System.currentTimeMillis() - startTime, ip, userAgent, null);
                    return response.writeWith(Mono.just(buffer));
                });
    }

    /**
     * 记录API访问日志
     */
    private void logApiAccess(String apiKey, Long tenantId, String method, String path,
                             String requestBody, String responseBody, Integer statusCode,
                             Long duration, String ip, String userAgent, String errorMsg) {
        try {
            gatewayLogService.logApiAccessAsync(
                    null, apiKey, tenantId, method, path, requestBody, responseBody,
                    statusCode, duration, ip, userAgent, errorMsg
            );
        } catch (Exception e) {
            log.error("记录API访问日志失败", e);
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "";
        }
        // 多级代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
