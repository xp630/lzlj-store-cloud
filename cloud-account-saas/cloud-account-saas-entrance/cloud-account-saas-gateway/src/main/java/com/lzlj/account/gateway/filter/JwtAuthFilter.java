
package com.lzlj.account.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
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

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证过滤器
 *
 * 流程：
 * 1. 检查白名单路径（放行）
 * 2. 检查环境（非prod/test跳过鉴权）
 * 3. 从 Authorization 头提取 JWT Token
 * 4. 验证 Token 签名
 * 5. 解析用户信息，传递给下游服务
 * 6. Token 无效或缺失，返回 401
 */
@Slf4j
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    /**
     * JWT 签名密钥（从 Nacos 配置获取）
     * 必须与 auth 服务使用相同的密钥
     */
    @Value("${jwt.secret:}")
    private String jwtSecret;

    /**
     * 需要鉴权的环境
     * test 和 prod 环境需要 JWT 鉴权
     */
    private static final List<String> AUTH_ENABLED_ENVIRONMENTS = Arrays.asList("test", "prod");

    /**
     * 当前激活的环境
     */
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * 白名单路径 - 不需要认证的路径
     */
    private static final List<String> WHITELIST_PATHS = Arrays.asList(
            "/user/login",           // 用户登录
            "/user/register",        // 用户注册
            "/doc.html",             // Swagger 文档
            "/swagger-ui",           // Swagger UI
            "/swagger-ui/",
            "/v3/api-docs",          // OpenAPI 文档
            "/actuator/health",      // 健康检查
            "/favicon.ico",
            "/api/saas-auth/user/login"  // 网关路由后的登录路径
    );

    /**
     * 用户信息 Header 名称（传递给下游服务）
     */
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_TENANT_ID = "X-Tenant-Id";
    private static final String HEADER_USERNAME = "X-Username";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. 检查白名单
        if (isWhitelisted(path)) {
            log.debug("路径在白名单中，放行: {}", path);
            return chain.filter(exchange);
        }

        // 2. 非 prod/test 环境，跳过鉴权
        if (!AUTH_ENABLED_ENVIRONMENTS.contains(activeProfile)) {
            log.debug("当前环境 [{}] 不需要鉴权，放行: {}", activeProfile, path);
            return chain.filter(exchange);
        }

        // 3. 提取 Token
        String authHeader = request.getHeaders().getFirst("Authorization");
        String token = extractToken(authHeader);

        if (!StringUtils.hasText(token)) {
            log.warn("路径需要认证，但未提供 Token: {}", path);
            return unauthorized(exchange, "未提供认证 Token");
        }

        // 3. 验证并解析 Token
        try {
            Claims claims = parseToken(token);

            // 4. 提取用户信息
            Long userId = claims.get("userId", Long.class);
            Long tenantId = claims.get("tenantId", Long.class);
            String username = claims.get("username", String.class);

            if (userId == null) {
                // 尝试从 subject 获取
                String subject = claims.getSubject();
                if (StringUtils.hasText(subject)) {
                    userId = Long.parseLong(subject);
                }
            }

            if (userId == null) {
                log.warn("Token 中缺少用户 ID");
                return unauthorized(exchange, "无效的 Token");
            }

            // 5. 将用户信息写入请求头，传递给下游服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(HEADER_USER_ID, String.valueOf(userId))
                    .header(HEADER_TENANT_ID, String.valueOf(tenantId != null ? tenantId : 0))
                    .header(HEADER_USERNAME, username != null ? username : "")
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            log.debug("JWT 认证成功: userId={}, username={}, path={}", userId, username, path);
            return chain.filter(mutatedExchange);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("Token 已过期: {}", path);
            return unauthorized(exchange, "Token 已过期");
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.warn("Token 格式错误: {}", path);
            return unauthorized(exchange, "Token 格式错误");
        } catch (Exception e) {
            log.warn("Token 验证失败: {}, error={}", path, e.getMessage());
            return unauthorized(exchange, "Token 无效或已过期");
        }
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhitelisted(String path) {
        return WHITELIST_PATHS.stream().anyMatch(pattern -> {
            if (pattern.endsWith("/")) {
                return path.startsWith(pattern) || path.equals(pattern.substring(0, pattern.length() - 1));
            }
            return path.startsWith(pattern) || path.equals(pattern);
        });
    }

    /**
     * 从 Authorization 头提取 Token
     */
    private String extractToken(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }

    /**
     * 解析并验证 Token
     */
    private Claims parseToken(String token) {
        if (!StringUtils.hasText(jwtSecret)) {
            throw new RuntimeException("JWT 密钥未配置");
        }

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 返回 401 未授权
     */
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
        return -99; // 在 TraceId 过滤器之后
    }
}
