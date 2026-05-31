window.onload = function() {
  window.ui = SwaggerUIBundle({
    urls: [
      { url: "/api/saas-auth/v3/api-docs", name: "认证模块 (saas-auth)" },
      { url: "/api/saas-goods/v3/api-docs", name: "商品模块 (saas-goods)" }
    ],
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
    configUrl: "/api/doc/v3/api-docs/swagger-config",
    validatorUrl: ""
  });
};
