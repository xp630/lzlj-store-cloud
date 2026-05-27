package com.lzlj.account.common.core.tenant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 租户上下文初始化过滤器
 * 从 X-Tenant-Id header 提取租户ID并设置到 TenantContext
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class TenantContextInitializerFilter extends OncePerRequestFilter {

    /**
     * 租户ID Header 名称
     */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, java.io.IOException {
        try {
            String tenantIdHeader = request.getHeader(HEADER_TENANT_ID);
            if (StringUtils.hasText(tenantIdHeader)) {
                try {
                    Long tenantId = Long.parseLong(tenantIdHeader);
                    TenantContext.setTenantId(tenantId);
                    log.debug("设置租户上下文: tenantId={}", tenantId);
                } catch (NumberFormatException e) {
                    log.warn("无效的租户ID格式: {}", tenantIdHeader);
                    TenantContext.setTenantId(0L);
                }
            } else {
                // 未提供租户ID，设置默认值为0
                TenantContext.setTenantId(0L);
            }
            filterChain.doFilter(request, response);
        } finally {
            // 请求完成后清除租户上下文
            TenantContext.clear();
        }
    }
}
