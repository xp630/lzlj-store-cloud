## Why

为 saas-gateway 和 lzlj-gateway 实现 Swagger API 文档聚合功能，同时优化数据权限边界处理（避免 @TableLogic 软删除与唯一键冲突）。

**解决的问题：**
1. 多个微服务各自暴露 Swagger 文档，前端需要访问多个端点
2. SaaS 模块 UserRole/RoleMenu 关联表在软删除后重新插入时出现唯一键冲突
3. lzlj-gateway 缺少 Swagger UI 入口

## What Changes

### Swagger 聚合
- 网关层新增 `/v3/api-docs/services` 端点，返回已注册服务列表
- 网关层新增 `/v3/api-docs/{serviceName}` 端点，代理到各服务的 OpenAPI 文档
- Swagger UI 页面从服务列表动态加载多个服务的 API 文档

### 数据权限优化
- UserRoleDao/RoleMenuDao 新增硬删除方法，绕过 @TableLogic 软删除
- saas_auth_user_role 表移除 tenant_id 列（数据库 schema 调整）
- 多表关联场景采用 N+1 查询模式，避免 JOIN 被租户拦截器污染

### API 文档增强
- Result/PageResult 等通用类添加 @Schema 注解
- UserLoginDTO/UserRoleDTO/RoleMenuDTO 等 DTO 添加详细 @Schema 描述

## Capabilities

### New Capabilities
- `gateway-swagger-aggregation`: 网关层聚合多个微服务的 Swagger/OpenAPI 文档
- `data-permission-boundary`: 明确多表 JOIN 场景下的数据权限边界处理策略

### Modified Capabilities
- (无)

## Impact

**网关模块：**
- cloud-account-saas-gateway: 新增 SwaggerAggregatorController, SwaggerUiConfig, SwaggerAggregatorConfig
- cloud-account-lzlj-gateway: 新增同样的 Swagger 聚合组件

**SaaS Auth 模块：**
- UserRoleDao: 新增 deleteByUserIdHard() 硬删除方法
- RoleMenuDao: 新增 deleteByRoleIdHard() 硬删除方法
- RoleServiceImpl/UserRoleServiceImpl: 调用硬删除方法替代软删除

**数据库：**
- saas_auth_user_role 表：移除 tenant_id 列，调整唯一键

**依赖：**
- 新增 springdoc-openapi-webflux-ui 依赖（gateway 模块）
