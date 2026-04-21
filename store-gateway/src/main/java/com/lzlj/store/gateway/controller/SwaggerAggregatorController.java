package com.lzlj.store.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Swagger 聚合控制器
 * 聚合各个微服务的 OpenAPI 3.0 文档
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v3/api-docs")
public class SwaggerAggregatorController {

    private final WebClient webClient;

    @Value("${swagger.services:store-user,store-goods,store-member,store-promotion,store-pay,store-trade,store-flashsale,store-search,store-data,store-file,store-delivery,store-settlement}")
    private List<String> services;

    /**
     * 获取指定服务的 OpenAPI 文档
     */
    @GetMapping("/{serviceName}")
    public Mono<Map<String, Object>> getApiDocs(@PathVariable String serviceName,
                                               ServerWebExchange exchange) {
        String scheme = exchange.getRequest().getHeaders().getFirst("X-Forwarded-Proto");
        if (scheme == null) {
            scheme = "http";
        }

        // 通过网关的路由前缀转发到对应服务
        // 例如: /v3/api-docs/store-user -> http://localhost:9092/v3/api-docs
        String baseUrl = scheme + "://" + exchange.getRequest().getHeaders().getFirst("Host");

        // 使用服务名直接拼接路由
        String servicePath = "/" + serviceName.toLowerCase();

        // 先尝试通过网关路由
        String gatewayUrl = baseUrl + "/services/" + servicePath + "/v3/api-docs";

        log.debug("获取 API Docs: service={}, url={}", serviceName, gatewayUrl);

        return webClient.get()
                .uri(gatewayUrl)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(doc -> {
                    // 处理 paths，添加服务前缀
                    Map<String, Object> result = new HashMap<>(doc);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> paths = (Map<String, Object>) result.get("paths");
                    if (paths != null) {
                        Map<String, Object> newPaths = new HashMap<>();
                        for (Map.Entry<String, Object> entry : paths.entrySet()) {
                            String newPath = servicePath + entry.getKey();
                            newPaths.put(newPath, entry.getValue());
                        }
                        result.put("paths", newPaths);
                    }
                    // 设置 server URL
                    result.put("server", baseUrl + servicePath);
                    return Mono.just(result);
                })
                .onErrorResume(e -> {
                    log.warn("获取服务 {} API Docs 失败: {}", serviceName, e.getMessage());
                    // 返回一个空的 OpenAPI 文档结构
                    Map<String, Object> emptyDoc = new HashMap<>();
                    emptyDoc.put("openapi", "3.0.1");
                    emptyDoc.put("info", Map.of(
                            "title", serviceName,
                            "description", "Service unavailable: " + e.getMessage()
                    ));
                    emptyDoc.put("paths", Map.of());
                    return Mono.just(emptyDoc);
                });
    }

    /**
     * 获取所有服务的聚合 OpenAPI 文档
     */
    @GetMapping
    public Mono<Map<String, Object>> getAllApiDocs(ServerWebExchange exchange) {
        String scheme = exchange.getRequest().getHeaders().getFirst("X-Forwarded-Proto");
        if (scheme == null) {
            scheme = "http";
        }
        String baseUrl = scheme + "://" + exchange.getRequest().getHeaders().getFirst("Host");

        // 聚合所有服务的文档
        List<Mono<Map<String, Object>>> docMonos = services.stream()
                .map(serviceName -> getServiceDoc(serviceName, exchange))
                .toList();

        return Mono.zip(docMonos, objects -> {
            Map<String, Object> merged = new HashMap<>();
            merged.put("openapi", "3.0.1");
            merged.put("info", Map.of(
                    "title", "LZLJ Cloud API",
                    "description", "聚合所有微服务 API 文档",
                    "version", "1.0.0"
            ));

            Map<String, Object> allPaths = new HashMap<>();
            for (Object obj : objects) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> doc = (Map<String, Object>) obj;
                    @SuppressWarnings("unchecked")
                    Map<String, Object> paths = (Map<String, Object>) doc.get("paths");
                    if (paths != null) {
                        allPaths.putAll(paths);
                    }
                }
            }
            merged.put("paths", allPaths);

            // 收集所有服务信息
            Map<String, Object> servers = new HashMap<>();
            for (String service : services) {
                servers.put(service, baseUrl + "/" + service.toLowerCase());
            }
            merged.put("servers", servers);

            return merged;
        });
    }

    private Mono<Map<String, Object>> getServiceDoc(String serviceName, ServerWebExchange exchange) {
        return getApiDocs(serviceName, exchange);
    }
}
