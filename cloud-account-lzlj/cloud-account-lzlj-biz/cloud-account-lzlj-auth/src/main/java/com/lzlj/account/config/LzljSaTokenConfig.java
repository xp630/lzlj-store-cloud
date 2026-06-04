package com.lzlj.account.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.lzlj.account.common.core.helper.RedisHelper;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.lzlj.account.permission.service.LzljPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * LZLJ Sa-Token 配置类
 * <p>
 * 配置基于 Redis 的 Token 存储和权限校验拦截器
 * <p>
 * 认证流程：
 * 1. Gateway 验证 JWT Token，解析用户信息，设置 X-User-Id 等请求头
 * 2. JwtToSatokenFilter 检测 X-User-Id，如存在则建立 Sa-Token Session
 * 3. Sa-Token 拦截器进行权限校验
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LzljSaTokenConfig implements WebMvcConfigurer {

    private final RedisHelper redisHelper;
    private final LzljPermissionService permissionService;

    /**
     * JWT 转 Sa-Token 桥接过滤器
     * <p>
     * 将 Gateway 的 JWT 认证转换为 Sa-Token Session，
     * 使得 Sa-Token 权限校验拦截器能够正常工作
     */
    @Bean
    public FilterRegistrationBean<JwtToSatokenFilter> jwtToSatokenFilter() {
        FilterRegistrationBean<JwtToSatokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtToSatokenFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10); // 在 Sa-Token 拦截器之前
        return registrationBean;
    }

    /**
     * JWT 转 Sa-Token 过滤器
     */
    public class JwtToSatokenFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // 检查 X-User-Id 请求头（由 Gateway 在 JWT 验证后设置）
            String userIdHeader = httpRequest.getHeader("X-User-Id");
            if (userIdHeader != null && !userIdHeader.isEmpty()) {
                try {
                    Long userId = Long.parseLong(userIdHeader);
                    // 检查是否已登录（避免重复创建 session）
                    if (!StpUtil.isLogin()) {
                        // 建立 Sa-Token Session
                        StpUtil.login(userId);
                        // 加载并缓存权限（供其他服务使用）
                        Set<String> permissions = permissionService.getUserPermissions(userId);
                        cacheUserPermissions(userId, permissions);
                        log.debug("JWT 桥接到 Sa-Token: userId={}, permissions={}", userId, permissions);
                    }
                } catch (NumberFormatException e) {
                    log.warn("无效的 X-User-Id header: {}", userIdHeader);
                }
            }

            chain.doFilter(request, response);
        }
    }

    /**
     * 添加 Sa-Token 权限拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            log.debug("Sa-Token 权限校验通过: userId={}, path={}",
                    StpUtil.getLoginId(), handle);
        }))
        .addPathPatterns("/api/**")
        .excludePathPatterns(
                "/api/lzlj-auth/user/login",
                "/api/lzlj-auth/user/sms-login",
                "/api/lzlj-auth/doc.html",
                "/api/lzlj-auth/swagger-ui/**",
                "/api/lzlj-auth/v3/api-docs/**"
        );

        log.info("LZLJ Sa-Token 权限拦截器已注册");
    }

    /**
     * 获取用户权限集合
     */
    @SuppressWarnings("unchecked")
    public Set<String> getUserPermissions(Long userId) {
        String key = "lzlj:user:permissions:" + userId;
        Set<String> permissions = redisHelper.get(key, Set.class);
        return permissions != null ? permissions : Collections.emptySet();
    }

    /**
     * 缓存用户权限
     */
    public void cacheUserPermissions(Long userId, Set<String> permissions) {
        String key = "lzlj:user:permissions:" + userId;
        redisHelper.set(key, permissions, 30, java.util.concurrent.TimeUnit.MINUTES);
    }

    /**
     * 清除用户权限缓存
     */
    public void clearUserPermissions(Long userId) {
        String key = "lzlj:user:permissions:" + userId;
        redisHelper.delete(key);
        log.info("清除用户权限缓存: userId={}", userId);
    }
}
