# LZLJ 远程调用 SaaS OpenAPI 接口

## Summary

统一 SaaS 对外暴露的接口路径，所有通过 AK/SK 鉴权的外部接口必须以 `/openapi` 开头。LZLJ 通过 OpenAPI 调用 SaaS 获取商户数据。

## Problem Statement

当前 SaaS 服务存在两类接口：
1. **内部接口** (`/merchant/**`) - 通过租户上下文隔离，适用于内部调用
2. **OpenAPI 接口** (`/openapi/**`) - 通过 API Key 鉴权

LZLJ 需要通过 OpenAPI 调用 SaaS，但目前 OpenAPI 接口路径不完整，部分功能只能通过内部接口访问。

## Goals

1. **统一接口路径** - 所有对外接口必须通过 `/openapi` 前缀暴露
2. **安全隔离** - OpenAPI 接口通过 AK/SK 鉴权，通过 API Key 隐含租户
3. **支持 LZLJ 调用** - LZLJ 可通过 OpenAPI 获取商户等数据

## Non-Goals

1. 不改变现有内部接口的租户隔离逻辑
2. 不实现 OAuth 等其他认证方式

## OpenAPI + 租户隔离流程（已就绪）

```
1. LZLJ 调用 /api/saas-auth/openapi/merchant/code/xxx
           (带 X-API-Key, X-Timestamp, X-Signature headers)
        ↓
2. SaaS Gateway OpenApiAuthFilter 验证 API Key
        ↓
3. 从 ApiKeyAuthInfo 获取 tenantId
        ↓
4. Gateway 转发请求，设置 X-Tenant-Id: {tenantId}
        ↓
5. SaaS Auth 服务 TenantContextInitializerFilter 读取 header
        ↓
6. 设置 TenantContext，MyBatis-Plus 租户拦截器使用 TenantContext.getTenantId()
```

**关键组件：**
- `ApiKeyAuthInfo.tenantId` - API Key 关联的租户ID
- `OpenApiAuthFilter` - 设置 X-Tenant-Id header
- `TenantContextInitializerFilter` - 读取 header 并设置 TenantContext

## 需要完成的工作

1. **创建 OpenAPI Controller** - 封装需要对外暴露的接口
2. **验证租户隔离** - 确保 OpenAPI 调用时 tenantId 正确传递
3. **修复 ignoreInsert 逻辑** - 解决 `tenant_id = tenant_id` 问题

## Related

- 原 OpenSpec: `lzlj-merchant-management`
