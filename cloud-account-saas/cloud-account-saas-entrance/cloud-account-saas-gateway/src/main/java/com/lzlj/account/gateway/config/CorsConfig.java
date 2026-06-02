package com.lzlj.account.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * CORS 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
                CorsConfiguration config = new CorsConfiguration();
                String origin = exchange.getRequest().getHeaders().getOrigin();
                if (origin != null && !origin.isEmpty()) {
                    config.setAllowedOriginPatterns(Arrays.asList(origin));
                } else {
                    config.setAllowedOriginPatterns(Arrays.asList("*"));
                }
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
                config.setAllowedHeaders(Arrays.asList("*"));
                config.setExposedHeaders(Arrays.asList("*"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L);
                return config;
            }
        };
        source.registerCorsConfiguration("/**", new CorsConfiguration());
        return new CorsWebFilter(source);
    }

    /**
     * 处理 OPTIONS 预检请求的 Filter
     * 确保预检请求被正确响应而不是转发到后端
     */
    @Bean
    public WebFilter corsOptionsFilter() {
        return (ServerWebExchange exchange, org.springframework.web.server.WebFilterChain chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 添加 CORS 头到所有响应
            String origin = request.getHeaders().getOrigin();
            if (origin != null && !origin.isEmpty()) {
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
            }

            // 处理 OPTIONS 预检请求，直接返回 200
            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }

            return chain.filter(exchange);
        };
    }
}
