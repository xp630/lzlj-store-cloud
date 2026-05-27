package com.lzlj.account.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * OpenAPI 认证过滤器
 * 直接转发已验签的请求到后端服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenApiAuthFilter implements GlobalFilter, Ordered {

    private final WebClient.Builder webClientBuilder;

    @Value("${openapi.signature.expire-seconds:300}")
    private int signatureExpireSeconds;

    private static final String OPENAPI_PATH_PREFIX = "/openapi/";
    private static final String AUTH_SERVICE_URL = "http://localhost:9092";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 检查是否是 OpenAPI 请求
        if (!path.startsWith(OPENAPI_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        // 2. 提取签名参数
        String apiKey = request.getHeaders().getFirst("X-API-Key");
        String timestampStr = request.getHeaders().getFirst("X-Timestamp");
        String signature = request.getHeaders().getFirst("X-Signature");
        String method = request.getMethod().name();

        // 3. 验证参数完整性
        if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(timestampStr) || !StringUtils.hasText(signature)) {
            return unauthorized(exchange, "缺少签名参数");
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            return unauthorized(exchange, "无效的时间戳格式");
        }

        // 4. 调用 auth 服务查询认证信息
        return webClientBuilder.build()
                .get()
                .uri(AUTH_SERVICE_URL + "/openapi/key/inner/auth/" + apiKey)
                .retrieve()
                .bodyToMono(ApiKeyAuthInfo.class)
                .flatMap(authInfo -> {
                    if (authInfo == null) {
                        log.warn("API Key 不存在: apiKey={}", apiKey);
                        return unauthorized(exchange, "API Key 无效");
                    }

                    // 5. 解密 secret
                    String secret;
                    try {
                        secret = new String(Base64.getDecoder().decode(authInfo.getApiSecret()));
                    } catch (Exception e) {
                        log.error("解密 secret 失败: apiKey={}", apiKey);
                        return unauthorized(exchange, "API Key 无效");
                    }

                    // 6. 验签
                    boolean verified = SignatureUtils.verify(
                            timestamp, method, path, null, secret, signature, signatureExpireSeconds
                    );

                    if (!verified) {
                        log.warn("OpenAPI 签名验证失败: apiKey={}, path={}", apiKey, path);
                        return unauthorized(exchange, "签名验证失败");
                    }

                    log.debug("OpenAPI 认证成功: apiKey={}, tenantId={}, path={}", apiKey, authInfo.getTenantId(), path);

                    // 7. 验签通过，直接转发请求到后端服务
                    return forwardToBackend(exchange, authInfo, path);
                })
                .onErrorResume(e -> {
                    log.error("调用认证服务失败: apiKey={}, error={}", apiKey, e.getMessage());
                    return unauthorized(exchange, "API Key 无效");
                });
    }

    /**
     * 直接转发请求到后端服务
     */
    private Mono<Void> forwardToBackend(ServerWebExchange exchange, ApiKeyAuthInfo authInfo, String originalPath) {
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
                .uri(AUTH_SERVICE_URL + originalPath)
                .headers(h -> h.putAll(headers))
                .retrieve()
                .bodyToMono(byte[].class)
                .flatMap(body -> {
                    response.getHeaders().setContentLength((long) body.length);
                    DataBuffer buffer = response.bufferFactory().wrap(body);
                    return response.writeWith(Mono.just(buffer));
                });
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
