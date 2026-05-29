package com.lzlj.account.common.filter;

import com.lzlj.account.common.core.context.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JWT 上下文过滤器
 * 从请求头中读取 X-User-Id, X-Username 并设置到 UserContext
 * 网关会通过这些头传递用户信息
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtContextFilter implements Filter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // 优先从网关传递的头信息获取
            String userId = httpRequest.getHeader(HEADER_USER_ID);
            String username = httpRequest.getHeader(HEADER_USERNAME);

            if (StringUtils.hasText(userId)) {
                UserContext.setUserId(Long.parseLong(userId));
            }
            if (StringUtils.hasText(username)) {
                UserContext.setUsername(username);
            }

            // 如果头信息中没有，尝试从 Authorization 头解析 JWT
            if (!StringUtils.hasText(userId)) {
                String authHeader = httpRequest.getHeader("Authorization");
                if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7).trim();
                    try {
                        Claims claims = parseToken(token);
                        String subject = claims.getSubject();
                        if (StringUtils.hasText(subject)) {
                            UserContext.setUserId(Long.parseLong(subject));
                        }
                        String claimUsername = claims.get("username", String.class);
                        if (StringUtils.hasText(claimUsername)) {
                            UserContext.setUsername(claimUsername);
                        }
                        // 解析 orgId
                        Long orgId = claims.get("orgId", Long.class);
                        if (orgId != null) {
                            UserContext.setOrgId(orgId);
                        }
                    } catch (Exception e) {
                        log.debug("JWT 解析失败: {}", e.getMessage());
                    }
                }
            }

            chain.doFilter(httpRequest, httpResponse);

        } finally {
            // 清理 ThreadLocal
            UserContext.clear();
        }
    }

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
}
