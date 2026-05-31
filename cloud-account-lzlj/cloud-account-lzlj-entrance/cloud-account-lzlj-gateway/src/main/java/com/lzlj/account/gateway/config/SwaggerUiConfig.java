package com.lzlj.account.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Swagger UI 静态资源配置
 */
@Configuration
public class SwaggerUiConfig {

    @Bean
    public RouterFunction<ServerResponse> swaggerRouter() {
        return RouterFunctions.route()
                .GET("/swagger-ui.html", request ->
                        ServerResponse.ok()
                                .contentType(MediaType.TEXT_HTML)
                                .bodyValue(getSwaggerUiHtml()))
                .GET("/doc.html", request ->
                        ServerResponse.ok()
                                .contentType(MediaType.TEXT_HTML)
                                .bodyValue(getSwaggerUiHtml()))
                .build();
    }

    private String getSwaggerUiHtml() {
        return "<!DOCTYPE html>\n" +
"<html>\n" +
"<head>\n" +
"    <meta charset=\"UTF-8\">\n" +
"    <title>LZLJ Cloud API 文档</title>\n" +
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
"        fetch('/v3/api-docs/services')\n" +
"            .then(function(res){ return res.json(); })\n" +
"            .then(function(data){\n" +
"                var urls = data.services.map(function(s){\n" +
"                    return { url: '/v3/api-docs/' + s, name: s };\n" +
"                });\n" +
"                window.ui = SwaggerUIBundle({\n" +
"                    urls: urls,\n" +
"                    dom_id: '#swagger-ui',\n" +
"                    deepLinking: true,\n" +
"                    presets: [\n" +
"                        SwaggerUIBundle.presets.apis,\n" +
"                        SwaggerUIStandalonePreset\n" +
"                    ],\n" +
"                    plugins: [\n" +
"                        SwaggerUIBundle.plugins.DownloadUrl\n" +
"                    ],\n" +
"                    layout: \"StandaloneLayout\",\n" +
"                    docExpansion: \"list\",\n" +
"                    filter: true,\n" +
"                    showExtensions: true,\n" +
"                    showCommonExtensions: true\n" +
"                });\n" +
"            });\n" +
"    </script>\n" +
"</body>\n" +
"</html>";
    }
}
