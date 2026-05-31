package com.lzlj.account.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Swagger 文档动态代理过滤器
 * 拦截 /services/{serviceName}/v3/api-docs 请求
 * 动态代理到对应后端服务的 /v3/api-docs
 *
 * 路由示例：
 *   /services/saas-auth/v3/api-docs   →   saas-auth:9092/v3/api-docs
 *   /services/saas-goods/v3/api-docs  →   saas-goods:xxxx/v3/api-docs
 */
@Slf4j
@Component
public class SwaggerDocProxyFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;

    @Value("${swagger.services:}")
    private String swaggerServices;

    public SwaggerDocProxyFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 只拦截 /services/{service}/v3/api-docs 格式的请求
        if (!path.matches("/services/[^/]+/v3/api-docs")) {
            return chain.filter(exchange);
        }

        // 提取服务名：/services/saas-auth/v3/api-docs → saas-auth
        String serviceName = path.replaceFirst("/services/", "").replaceFirst("/v3/api-docs", "");
        String targetPath = "/v3/api-docs";

        // 检查是否是已配置的服务
        if (swaggerServices != null && !swaggerServices.contains(serviceName)) {
            log.warn("SwaggerDocProxy: service not configured, service={}", serviceName);
            return chain.filter(exchange);
        }

        log.debug("SwaggerDocProxy: forwarding {} → {}{}", path, serviceName, targetPath);

        // 动态构建目标 URL 并转发
        String finalPath = targetPath;
        return webClient.get()
                .uri("http://" + serviceName + finalPath)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(body -> {
                                    exchange.getResponse().getHeaders().putAll(
                                            clientResponse.headers().asHttpHeaders()
                                    );
                                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                    return exchange.getResponse().writeWith(
                                            Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes()))
                                    );
                                });
                    } else {
                        return clientResponse.createException().flatMap(Mono::error);
                    }
                })
                .onErrorResume(e -> {
                    log.error("SwaggerDocProxy: forward failed, service={}, error={}", serviceName, e.getMessage());
                    return chain.filter(exchange);
                });
    }

    @Override
    public int getOrder() {
        // 在 JwtAuthFilter 之前执行
        return -99;
    }
}
