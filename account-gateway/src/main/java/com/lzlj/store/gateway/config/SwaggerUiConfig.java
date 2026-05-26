package com.lzlj.account.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        return "<!DOCTYPE html>\n" +
"<html>\n" +
"<head>\n" +
"    <meta charset=\"UTF-8\">\n" +
"    <title>LZLJ Cloud API Documentation</title>\n" +
"    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://unpkg.com/swagger-ui-dist@5.10.5/swagger-ui.css\">\n" +
"    <style>\n" +
"        body { margin: 0; padding: 0; }\n" +
"        .swagger-ui .topbar { display: none; }\n" +
"        .info .title { font-size: 24px !important; }\n" +
"        .scheme-container { background: #fafafa !important; }\n" +
"    </style>\n" +
"</head>\n" +
"<body>\n" +
"    <div id=\"swagger-ui\"></div>\n" +
"    <script src=\"https://unpkg.com/swagger-ui-dist@5.10.5/swagger-ui-bundle.js\"></script>\n" +
"    <script src=\"https://unpkg.com/swagger-ui-dist@5.10.5/swagger-ui-standalone-preset.js\"></script>\n" +
"    <script>\n" +
"        window.onload = function() {\n" +
"            SwaggerUIBundle({\n" +
"                url: \"/v3/api-docs\",\n" +
"                dom_id: '#swagger-ui',\n" +
"                deepLinking: true,\n" +
"                presets: [\n" +
"                    SwaggerUIBundle.presets.apis,\n" +
"                    SwaggerUIStandalonePreset\n" +
"                ],\n" +
"                plugins: [\n" +
"                    SwaggerUIBundle.plugins.DownloadUrl\n" +
"                ],\n" +
"                layout: \"StandaloneLayout\",\n" +
"                docExpansion: \"list\",\n" +
"                filter: true,\n" +
"                showExtensions: true,\n" +
"                showCommonExtensions: true\n" +
"            });\n" +
"        };\n" +
"    </script>\n" +
"</body>\n" +
"</html>";
    }

    private String getApiDocsIndex() {
        return "{\n" +
"    \"openapi\": \"3.0.1\",\n" +
"    \"info\": {\n" +
"        \"title\": \"LZLJ Cloud API\",\n" +
"        \"description\": \"泸州老窖云店系统 API\",\n" +
"        \"version\": \"1.0.0\"\n" +
"    },\n" +
"    \"paths\": {\n" +
"        \"/services/store-user\": {\n" +
"            \"get\": {\n" +
"                \"summary\": \"用户服务\",\n" +
"                \"description\": \"用户账户、组织架构、门店管理\"\n" +
"            }\n" +
"        },\n" +
"        \"/services/store-goods\": {\n" +
"            \"get\": {\n" +
"                \"summary\": \"商品服务\",\n" +
"                \"description\": \"商品SPU/SKU、分类、搜索\"\n" +
"            }\n" +
"        },\n" +
"        \"/services/store-member\": {\n" +
"            \"get\": {\n" +
"                \"summary\": \"会员服务\",\n" +
"                \"description\": \"会员等级、积分、权益\"\n" +
"            }\n" +
"        }\n" +
"    },\n" +
"    \"servers\": [\n" +
"        {\"url\": \"http://localhost:18080\", \"description\": \"网关入口\"}\n" +
"    ]\n" +
"}";
    }
}
