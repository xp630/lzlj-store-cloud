## Context

项目采用 Spring Cloud Gateway 作为微服务网关，每个微服务独立使用 springdoc-openapi 生成 Swagger 文档。前端需要访问多个服务的 API 文档，缺乏统一的入口。

同时，SaaS 模块的权限系统涉及 User、Role、Menu、UserRole、RoleMenu 等多表关联，存在租户拦截器自动注入 tenant_id 的边界问题。

## Goals / Non-Goals

**Goals:**
- 在网关层聚合多个服务的 Swagger/OpenAPI 文档
- 提供统一的 `/swagger-ui.html` 入口
- 解决 @TableLogic 软删除与唯一键约束的冲突
- 明确多表 JOIN 场景下的数据权限边界

**Non-Goals:**
- 不修改现有微服务的 Swagger 配置
- 不实现跨租户数据查询
- 不引入新的权限框架

## Decisions

### 1. Swagger 聚合架构

**方案：网关层 Controller 代理**

```
Client → Gateway:28080/swagger-ui.html
       ↓
Gateway 返回静态 HTML（动态加载服务列表）
       ↓
Client fetch /v3/api-docs/services → ["saas-auth", "saas-goods"]
       ↓
Client fetch /v3/api-docs/saas-auth → 网关代理到 saas-auth 服务
```

**关键实现点：**

| 组件 | 职责 |
|------|------|
| SwaggerUiConfig | 提供 `/swagger-ui.html` 静态页面 |
| SwaggerAggregatorController | `/v3/api-docs/services` 返回服务列表 |
| SwaggerAggregatorController | `/v3/api-docs/{service}` 代理到各服务 |
| SwaggerAggregatorConfig | WebClient Bean 配置 |

**服务发现方式：**
- 使用 `exchange.getRequest().getHeaders().getFirst("Host")` 获取请求 Host
- 拼接绝对 URL：`scheme + "://" + host + "/api/" + serviceLower + "/v3/api-docs"`

### 2. 路由配置与 Nacos 分离

**问题：** Spring Cloud Gateway 启动时 routes 先于 Nacos 配置加载，导致 Nacos 中的路由配置不生效。

**解决：** 路由配置放在本地 `application-dev.yml`，Nacos 仅管理应用级配置。

```
本地 application-dev.yml  →  路由配置 + swagger.services
Nacos xxx-gateway.yml    →  openapi.* 配置
```

### 3. @TableLogic 软删除与唯一键冲突

**问题：** MySQL InnoDB 软删除行仍占据唯一键槽位，重新插入相同唯一键值时报 `Duplicate entry` 错误。

**场景：** UserRole 关联表 (user_id, role_id) 有唯一键，用户解绑角色后立即重新绑定会触发冲突。

**解决：** 关联表（UserRole, RoleMenu）使用硬删除。

```java
@Update("DELETE FROM saas_auth_user_role WHERE user_id = #{userId}")
int deleteByUserIdHard(@Param("userId") Long userId);
```

### 4. 数据权限边界 - N+1 查询模式

**问题：** 多表 JOIN 会被租户拦截器自动注入 `WHERE tenant_id = ?`，导致 JOIN 失败或数据错乱。

**决策：**
- 平台级实体（Menu, Role, UserRole, RoleMenu）不继承 TenantEntity，不带 tenant_id
- 用户实体（User）属于租户级，继承 TenantEntity
- 多表查询采用 N+1 模式：在代码中分步查询，避免 SQL JOIN

```java
// N+1 模式示例
List<Role> roles = roleDao.selectList(...);  // 查询角色
for (Role role : roles) {
    List<Menu> menus = menuDao.selectByRoleId(role.getId());  // 每角色查菜单
}
```

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| N+1 查询性能 | 多表查询增加数据库往返 | 小团队低并发场景可接受；后续可引入本地缓存 |
| 硬删除数据不可恢复 | 关联表删除后无审计 | 关联表本身无敏感数据；操作日志记录删除行为 |
| Swagger 依赖服务可用性 | 某服务挂了影响文档展示 | 错误时返回空 paths + 错误提示 |
