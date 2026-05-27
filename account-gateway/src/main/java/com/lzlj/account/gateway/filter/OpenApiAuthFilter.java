package com.lzlj.account.gateway.filter;

import com.lzlj.account.common.core.utils.SignatureUtils;
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

/**
 * OpenAPI 认证过滤器
 *
 * 流程：
 * 1. 检查路径是否以 /openapi/ 开头（是的则走 OpenAPI 认证）
 * 2. 验证时间戳防重放
 * 3. 验证签名
 * 4. 通过后查询 API Key 对应的 tenantId，设置到 Header
 * 5. 验签失败返回 401
 */
@Slf4j
@Component
public class OpenApiAuthFilter implements GlobalFilter, Ordered {

    /**
     * OpenAPI 签名有效期（秒）
     */
    @Value("${openapi.signature.expire-seconds:300}")
    private int signatureExpireSeconds;

    /**
     * OpenAPI 路径前缀
     */
    private static final String OPENAPI_PATH_PREFIX = "/openapi/";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 检查是否是 OpenAPI 请求（通过路径判断）
        if (!path.startsWith(OPENAPI_PATH_PREFIX)) {
            // 不是 OpenAPI 请求，放行让后续 JwtAuthFilter 处理
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

        // 4. 验签
        // TODO: 通过 Feign 调用 auth 服务查询 apiKey 对应的 secret
        // 暂时先放行，生产环境需要实现
        String secret = "sk_test_secret"; // 临时占位，实际应从 auth 服务查询

        boolean verified = SignatureUtils.verify(
            timestamp, method, path, null, secret, signature, signatureExpireSeconds
        );

        if (!verified) {
            log.warn("OpenAPI 签名验证失败: apiKey={}, path={}", apiKey, path);
            return unauthorized(exchange, "签名验证失败");
        }

        // 5. 签名验证通过，查询 tenantId 并设置到 Header
        // TODO: 通过 Feign 调用 auth 服务查询 apiKey 对应的 tenantId
        Long tenantId = 1L; // 临时占位，实际应从 auth 服务查询

        // 6. 将用户信息写入请求头，传递给下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", "0")                    // API请求无用户ID
                .header("X-Tenant-Id", String.valueOf(tenantId))
                .header("X-Username", "openapi")
                .header("X-API-Key", apiKey)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        log.debug("OpenAPI 认证成功: apiKey={}, tenantId={}, path={}", apiKey, tenantId, path);
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
        return -100; // 在 JwtAuthFilter 之前执行
    }
}
