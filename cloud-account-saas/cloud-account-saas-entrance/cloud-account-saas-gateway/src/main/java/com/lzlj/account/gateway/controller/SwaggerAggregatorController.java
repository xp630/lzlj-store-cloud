package com.lzlj.account.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Value("${swagger.services:}")
    private List<String> services;

    /**
     * 获取指定服务的 OpenAPI 文档
     * 通过网关现有路由 /api/{service}/** 转发到后端服务
     */
    @GetMapping("/{serviceName}")
    public Mono<Map<String, Object>> getApiDocs(@PathVariable String serviceName,
                                               ServerWebExchange exchange) {
        String serviceLower = serviceName.toLowerCase();

        // 从请求 Header 动态获取 host，构造绝对 URL
        String host = exchange.getRequest().getHeaders().getFirst("Host");
        if (host == null || host.isEmpty()) {
            host = "localhost:18080";
        }
        String scheme = exchange.getRequest().getHeaders().getFirst("X-Forwarded-Proto");
        if (scheme == null) {
            scheme = "http";
        }
        String baseUrl = scheme + "://" + host;
        String targetUrl = baseUrl + "/api/" + serviceLower + "/v3/api-docs";
        String serverUrl = baseUrl + "/api/" + serviceLower;

        log.debug("获取 API Docs: service={}, url={}", serviceName, targetUrl);

        return webClient.get()
                .uri(targetUrl)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(doc -> {
                    Map<String, Object> result = new HashMap<>(doc);
                    // 设置 servers 为网关地址数组（OpenAPI 3.0 规范）
                    Map<String, Object> serverObj = new HashMap<>();
                    serverObj.put("url", serverUrl);
                    serverObj.put("description", "Gateway");
                    result.put("servers", Collections.singletonList(serverObj));
                    return Mono.just(result);
                })
                .onErrorResume(e -> {
                    log.warn("获取服务 {} API Docs 失败: {}", serviceName, e.getMessage());
                    Map<String, Object> emptyDoc = new HashMap<>();
                    emptyDoc.put("openapi", "3.0.1");
                    Map<String, Object> info = new HashMap<>();
                    info.put("title", serviceName);
                    info.put("description", "Service unavailable: " + e.getMessage());
                    emptyDoc.put("info", info);
                    emptyDoc.put("paths", Collections.emptyMap());
                    Map<String, Object> serverObj = new HashMap<>();
                    serverObj.put("url", serverUrl);
                    serverObj.put("description", "Gateway");
                    emptyDoc.put("servers", Collections.singletonList(serverObj));
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
                .collect(Collectors.toList());

        return Mono.zip(docMonos, objects -> {
            Map<String, Object> merged = new HashMap<>();
            merged.put("openapi", "3.0.1");
            Map<String, Object> info = new HashMap<>();
            info.put("title", "LZLJ Cloud API");
            info.put("description", "聚合所有微服务 API 文档");
            info.put("version", "1.0.0");
            merged.put("info", info);

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

            // 设置 servers 为网关地址
            merged.put("server", baseUrl);

            return merged;
        });
    }

    private Mono<Map<String, Object>> getServiceDoc(String serviceName, ServerWebExchange exchange) {
        return getApiDocs(serviceName, exchange);
    }

    /**
     * 返回服务列表（供 Swagger UI 动态生成下拉框）
     */
    @GetMapping("/services")
    public Mono<Map<String, Object>> getServices() {
        Map<String, Object> result = new HashMap<>();
        result.put("services", services);
        return Mono.just(result);
    }
}
