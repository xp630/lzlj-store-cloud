package com.lzlj.store.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Duration;

/**
 * Swagger UI 静态资源配置
 * 网关层提供 Swagger UI 和 OpenAPI 文档聚合
 */
@Configuration
public class SwaggerUiConfig {

    @Bean
    public RouterFunction<ServerResponse> swaggerRouter() {
        return RouterFunctions.route()
                // Swagger UI 主页面
                .GET("/swagger-ui.html", request ->
                        ServerResponse.ok()
                                .contentType(MediaType.TEXT_HTML)
                                .bodyValue(getSwaggerUiHtml()))
                // Redirect /doc.html to swagger-ui
                .GET("/doc.html", request ->
                        ServerResponse.ok()
                                .contentType(MediaType.TEXT_HTML)
                                .bodyValue(getSwaggerUiHtml()))
                // OpenAPI 3.0 聚合文档
                .GET("/v3/api-docs", request -> {
                    // 返回聚合的 API 列表页面
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(getApiDocsIndex());
                })
                .build();
    }

    private String getSwaggerUiHtml() {
        return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>LZLJ Cloud API Documentation</title>
    <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist@5.10.5/swagger-ui.css">
    <style>
        body { margin: 0; padding: 0; }
        .swagger-ui .topbar { display: none; }
        .info .title { font-size: 24px !important; }
        .scheme-container { background: #fafafa !important; }
    </style>
</head>
<body>
    <div id="swagger-ui"></div>
    <script src="https://unpkg.com/swagger-ui-dist@5.10.5/swagger-ui-bundle.js"></script>
    <script src="https://unpkg.com/swagger-ui-dist@5.10.5/swagger-ui-standalone-preset.js"></script>
    <script>
        window.onload = function() {
            SwaggerUIBundle({
                url: "/v3/api-docs",
                dom_id: '#swagger-ui',
                deepLinking: true,
                presets: [
                    SwaggerUIBundle.presets.apis,
                    SwaggerUIStandalonePreset
                ],
                plugins: [
                    SwaggerUIBundle.plugins.DownloadUrl
                ],
                layout: "StandaloneLayout",
                docExpansion: "list",
                filter: true,
                showExtensions: true,
                showCommonExtensions: true
            });
        };
    </script>
</body>
</html>
                """;
    }

    private String getApiDocsIndex() {
        return """
{
    "openapi": "3.0.1",
    "info": {
        "title": "LZLJ Cloud API",
        "description": "泸州老窖云店系统 API",
        "version": "1.0.0"
    },
    "paths": {
        "/services/store-user": {
            "get": {
                "summary": "用户服务",
                "description": "用户账户、组织架构、门店管理"
            }
        },
        "/services/store-goods": {
            "get": {
                "summary": "商品服务",
                "description": "商品SPU/SKU、分类、搜索"
            }
        },
        "/services/store-member": {
            "get": {
                "summary": "会员服务",
                "description": "会员等级、积分、权益"
            }
        }
    },
    "servers": [
        {"url": "http://localhost:18080", "description": "网关入口"}
    ]
}
                """;
    }
}
