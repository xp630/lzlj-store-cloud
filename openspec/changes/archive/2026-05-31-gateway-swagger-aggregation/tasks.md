## 1. SaaS Gateway Swagger 聚合

- [x] 1.1 添加 springdoc-openapi-webflux-ui 依赖 (saas-gateway/pom.xml)
- [x] 1.2 创建 SwaggerAggregatorConfig (WebClient 配置)
- [x] 1.3 创建 SwaggerAggregatorController (服务列表 + 代理端点)
- [x] 1.4 修改 SwaggerUiConfig (移除 /v3/api-docs 阻塞，保留 /swagger-ui.html)
- [x] 1.5 配置 application-dev.yml (路由 + swagger.services)
- [x] 1.6 验证 Swagger UI 多服务下拉框

## 2. LZLJ Gateway Swagger 聚合

- [x] 2.1 添加 springdoc-openapi-webflux-ui 依赖 (lzlj-gateway/pom.xml)
- [x] 2.2 创建 SwaggerAggregatorConfig
- [x] 2.3 创建 SwaggerAggregatorController
- [x] 2.4 创建 SwaggerUiConfig
- [x] 2.5 配置 application-dev.yml (路由 + swagger.services)
- [x] 2.6 验证 http://localhost:28080/swagger-ui.html

## 3. 数据权限优化

- [x] 3.1 UserRoleDao 添加 deleteByUserIdHard() 硬删除方法
- [x] 3.2 UserRoleServiceImpl 改用硬删除
- [x] 3.3 RoleMenuDao 添加 deleteByRoleIdHard() 硬删除方法
- [x] 3.4 RoleServiceImpl 改用硬删除 (2处)
- [x] 3.5 创建 SQL 迁移脚本 (saas_auth_user_role 移除 tenant_id)
- [x] 3.6 创建 CODE_REVIEW_CHECKLIST (多表查询规范)

## 4. API 文档增强

- [x] 4.1 Result 类添加 @Schema 注解
- [x] 4.2 PageResult 类添加 @Schema 注解
- [x] 4.3 UserLoginDTO 添加 @Schema 注解
- [x] 4.4 UserRoleDTO 添加 @Schema 注解
- [x] 4.5 RoleMenuDTO 添加 @Schema 注解
- [x] 4.6 更新 Controller @Operation 描述 (全量替换说明)

## 5. 配置整理

- [x] 5.1 Nacos saas-gateway.yml 仅保留 openapi.* 配置
- [x] 5.2 Nacos lzlj-gateway.yml 仅保留 openapi.* 配置
- [x] 5.3 清理未使用的 static/swagger-ui/ 静态文件
