package com.lzlj.account.gateway.filter;

import com.lzlj.account.gateway.config.AuthServiceUrlProvider;
import com.lzlj.account.gateway.log.ApiAccessLogger;
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
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * OpenAPI 认证过滤器
 */
@Slf4j
@Component
public class OpenApiAuthFilter implements GlobalFilter, Ordered {

    private static final String OPENAPI_PATH_PREFIX = "/openapi/";
    private static final String OPENAPI_BACKEND_PATH_PREFIX = "/openapi";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_API_KEY = "X-API-Key";

    private final WebClient.Builder webClientBuilder;
    private final ApiAccessLogger apiAccessLogger;
    private final AuthServiceUrlProvider authServiceUrlProvider;

    @Value("${openapi.signature.expire-seconds:300}")
    private int signatureExpireSeconds;

    public OpenApiAuthFilter(WebClient.Builder webClientBuilder, ApiAccessLogger apiAccessLogger,
                            AuthServiceUrlProvider authServiceUrlProvider) {
        this.webClientBuilder = webClientBuilder;
        this.apiAccessLogger = apiAccessLogger;
        this.authServiceUrlProvider = authServiceUrlProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 非 OpenAPI 请求跳过
        if (!path.startsWith(OPENAPI_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        // 2. 构建请求上下文
        RequestContext ctx = new RequestContext(
                request.getMethod().name(),
                path,
                getClientIp(request),
                request.getHeaders().getFirst(HttpHeaders.USER_AGENT)
        );

        // 3. 提取签名参数
        String apiKey = request.getHeaders().getFirst("X-API-Key");
        String timestampStr = request.getHeaders().getFirst("X-Timestamp");
        String signature = request.getHeaders().getFirst("X-Signature");

        // 4. 验证参数完整性
        if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(timestampStr) || !StringUtils.hasText(signature)) {
            ctx.apiKey = apiKey;
            return unauthorized(exchange, ctx, "缺少签名参数");
        }

        ctx.apiKey = apiKey;

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            return unauthorized(exchange, ctx, "无效的时间戳格式");
        }

        // 5. 获取 auth 服务地址
        String authServiceUrl = authServiceUrlProvider.getAuthServiceUrl();
        if (authServiceUrl == null) {
            return unauthorized(exchange, ctx, "认证服务不可用");
        }

        // 6. 调用 auth 服务查询认证信息并验签
        Mono<ApiKeyAuthInfo> authMono = fetchAuthInfo(authServiceUrl, apiKey);
        return authMono
                .flatMap(authInfo -> {
                    if (authInfo == null) {
                        return unauthorized(exchange, ctx, "API Key 无效");
                    }
                    return verifyAndForward(exchange, authInfo, timestamp, signature, ctx, authServiceUrl);
                })
                .onErrorResume(e -> unauthorized(exchange, ctx, "API Key 无效"));
    }

    /**
     * 调用 auth 服务获取认证信息
     */
    private Mono<ApiKeyAuthInfo> fetchAuthInfo(String authServiceUrl, String apiKey) {
        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/openapi/key/inner/auth/" + apiKey)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), response ->
                        Mono.error(new RuntimeException("Auth failed: " + response.statusCode())))
                .bodyToMono(ApiKeyAuthInfo.class)
                .switchIfEmpty(Mono.error(new RuntimeException("API Key 不存在")));
    }

    /**
     * 验签并转发请求
     */
    private Mono<Void> verifyAndForward(ServerWebExchange exchange, ApiKeyAuthInfo authInfo,
                                       long timestamp, String signature, RequestContext ctx, String authServiceUrl) {
        // 验证 API Key 有效性
        if (authInfo == null) {
            log.warn("API Key 不存在: apiKey={}", ctx.apiKey);
            return unauthorized(exchange, ctx, "API Key 无效");
        }

        // 解密 secret 并验签
        try {
            String secret = new String(Base64.getDecoder().decode(authInfo.getApiSecret()));
            boolean verified = SignatureUtils.verify(
                    timestamp, ctx.method, ctx.path, null, secret, signature, signatureExpireSeconds);

            if (!verified) {
                log.warn("OpenAPI 签名验证失败: apiKey={}, path={}", authInfo.getApiKey(), ctx.path);
                return unauthorized(exchange, ctx, "签名验证失败");
            }
        } catch (Exception e) {
            log.error("解密 secret 失败: apiKey={}", authInfo.getApiKey(), e);
            return unauthorized(exchange, ctx, "API Key 无效");
        }

        log.debug("OpenAPI 认证成功: apiKey={}, tenantId={}, path={}",
                authInfo.getApiKey(), authInfo.getTenantId(), ctx.path);

        // 转发请求到后端服务
        return forwardToBackend(exchange, authInfo, ctx, authServiceUrl);
    }

    /**
     * 转发请求到后端服务
     */
    private Mono<Void> forwardToBackend(ServerWebExchange exchange, ApiKeyAuthInfo authInfo,
                                       RequestContext ctx, String authServiceUrl) {
        ServerHttpResponse response = exchange.getResponse();
        String backendPath = ctx.path.substring(OPENAPI_BACKEND_PATH_PREFIX.length());

        // 构建转发 headers
        MultiValueMap<String, String> requestHeaders = exchange.getRequest().getHeaders();
        HttpHeaders forwardingHeaders = new HttpHeaders();
        forwardingHeaders.putAll(requestHeaders);

        // 设置用户上下文
        forwardingHeaders.set(HEADER_USER_ID, "0");
        forwardingHeaders.set(HEADER_TENANT_ID, String.valueOf(authInfo.getTenantId()));
        forwardingHeaders.set(HEADER_USERNAME, "openapi");
        forwardingHeaders.set(HEADER_API_KEY, authInfo.getApiKey());

        // 移除代理相关 headers
        forwardingHeaders.remove(HttpHeaders.HOST);
        forwardingHeaders.remove("X-Forwarded-For");
        forwardingHeaders.remove("X-Forwarded-Host");
        forwardingHeaders.remove("X-Forwarded-Port");
        forwardingHeaders.remove("X-Forwarded-Proto");

        // 发送请求
        return webClientBuilder.build()
                .method(exchange.getRequest().getMethod())
                .uri(authServiceUrl + backendPath)
                .headers(h -> h.putAll(forwardingHeaders))
                .retrieve()
                .bodyToMono(byte[].class)
                .flatMap(body -> {
                    // 记录成功日志
                    String responseBody = new String(body, StandardCharsets.UTF_8);
                    apiAccessLogger.log(
                            authInfo.getId(), authInfo.getApiKey(), authInfo.getTenantId(),
                            ctx.method, backendPath, null, responseBody, 200,
                            System.currentTimeMillis() - ctx.startTime, ctx.ip, ctx.userAgent, null);

                    response.getHeaders().setContentLength(body.length);
                    DataBuffer buffer = response.bufferFactory().wrap(body);
                    return response.writeWith(Mono.just(buffer));
                });
    }

    /**
     * 返回 401 响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, RequestContext ctx, String message) {
        log.info("unauthorized 被调用: apiKey={}, path={}, message={}", ctx.apiKey, ctx.path, message);
        // 记录失败日志
        apiAccessLogger.log(
                null, ctx.apiKey, 0L, ctx.method, ctx.path, null, null, 401,
                System.currentTimeMillis() - ctx.startTime, ctx.ip, ctx.userAgent, message);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
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
            ip = request.getRemoteAddress() != null
                    ? request.getRemoteAddress().getAddress().getHostAddress() : "";
        }
        // 多级代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @Override
    public int getOrder() {
        return -100;
    }

    /**
     * 请求上下文
     */
    private static class RequestContext {
        final String method;
        final String path;
        final String ip;
        final String userAgent;
        final long startTime;
        String apiKey;

        RequestContext(String method, String path, String ip, String userAgent) {
            this.method = method;
            this.path = path;
            this.ip = ip;
            this.userAgent = userAgent;
            this.startTime = System.currentTimeMillis();
        }
    }
}
