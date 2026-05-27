package com.lzlj.account.gateway.filter;

import com.lzlj.account.gateway.feign.ApiKeyFeignClient;
import com.lzlj.account.gateway.feign.ApiKeyAuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * OpenAPI 认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenApiAuthFilter implements GlobalFilter, Ordered {

    private final ApiKeyFeignClient apiKeyFeignClient;

    @Value("${openapi.signature.expire-seconds:300}")
    private int signatureExpireSeconds;

    private static final String OPENAPI_PATH_PREFIX = "/openapi/";

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

        // 4. 调用 auth 服务查询认证信息（带Redis缓存）
        ApiKeyAuthInfo authInfo;
        try {
            authInfo = apiKeyFeignClient.getAuthInfo(apiKey);
        } catch (Exception e) {
            log.error("调用认证服务失败: apiKey={}, error={}", apiKey, e.getMessage());
            return unauthorized(exchange, "API Key 无效");
        }

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

        // 7. 验签通过，设置用户信息
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", "0")
                .header("X-Tenant-Id", String.valueOf(authInfo.getTenantId()))
                .header("X-Username", "openapi")
                .header("X-API-Key", apiKey)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        log.debug("OpenAPI 认证成功: apiKey={}, tenantId={}, path={}", apiKey, authInfo.getTenantId(), path);
        return chain.filter(mutatedExchange);
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
