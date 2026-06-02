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
3. **~~修复 ignoreInsert 逻辑~~** ✅ 已修复 - `ignoreInsert` 返回 `true`，由 MetaObjectHandler 填充 tenant_id

---

## OpenAPI 接口清单（已完成）

### 商户接口

| 接口 | 路径 | 方法 | 说明 |
|------|------|------|------|
| 获取商户详情 | `/openapi/merchant/getById` | POST | body: `{"id": Long}` |
| 获取商户详情 | `/openapi/merchant/getByCode` | POST | body: `{"merchantCode": String}` |
| 分页查询商户 | `/openapi/merchant/page` | POST | body: `{"pageNum": Int, "pageSize": Int, "keyword": String, "status": Int}` |

### 支付通道接口

| 接口 | 路径 | 方法 | 说明 |
|------|------|------|------|
| 获取支付通道详情 | `/openapi/paymentChannel/getById` | POST | body: `{"id": Long}` |
| 分页查询支付通道 | `/openapi/paymentChannel/page` | POST | body: `{"pageNum": Int, "pageSize": Int, "channelName": String, "status": Int}` |
| 查询支付通道列表 | `/openapi/paymentChannel/list` | POST | body: `{"status": Int}` |

---

## 支付通道同步（已完成）

### 字段合并

LZLJ PaymentChannel 与 SaaS PaymentChannel 合并，以 SaaS 为准。

**移除的 LZLJ 特有字段：**
- `channelType` - SaaS 无此字段
- `description` - SaaS 无此字段
- `feeRate` / `minAmount` / `maxAmount` - SaaS 使用 `cloudAccountFee` 等替代

**合并后 LZLJ LzljPaymentChannel 字段：**
```
id, channelCode, channelName, paymentMethod, status,
cloudAccountFee, upstreamCostFee, totalFeeCost, perTransactionLimit
```

### 修改的文件

| 文件 | 变更 |
|------|------|
| `LzljPaymentChannel.java` | 移除 channelType, description, feeRate, minAmount, maxAmount；新增 cloudAccountFee, upstreamCostFee, totalFeeCost, perTransactionLimit |
| `LzljPaymentChannelDTO.java` | 标记 @deprecated，复用 common-core 的 PaymentChannelDTO |
| `LzljPaymentChannelQueryDTO.java` | 移除 channelType |
| `LzljPaymentChannelService.java` | `syncFromExternal()` → `syncFromSaas()`，返回 int |
| `LzljPaymentChannelServiceImpl.java` | 实现 `syncFromSaas()` |
| `LzljPaymentChannelController.java` | `POST /payment-channel/sync` 返回同步数量 |
| SQL 迁移 | `021_alter_lzlj_payment_channel_fields.sql` |

### 同步逻辑（全量同步）

```
1. 调用 SaaS OpenAPI: POST /openapi/paymentChannel/list (status=null 获取全部)
2. 查询本地所有未删除的通道
3. 对比找出差异：
   - 本地有、SaaS 没有 → 软删除 (deleted=1)
   - 本地没有、SaaS 有 → 插入新记录
   - 都有 → 更新
4. 返回新增/更新数量
```

### 中文编码修复

**问题**：RestTemplate 默认使用 ISO-8859-1 解码 HTTP 响应，中文乱码。

**修复**：`RestTemplateConfig.java` 显式设置 `StringHttpMessageConverter` 默认字符集为 UTF-8。

### XXL-JOB 调度

新增 `SaasSyncJobHandler`，支持以下任务：

| Handler | 说明 |
|---------|------|
| `paymentChannelSync` | 同步支付通道 |
| `merchantSync` | 同步商户 |
| `fullSaasSync` | 全量同步（支付通道+商户）|

### 修改的文件

| 文件 | 变更 |
|------|------|
| `RestTemplateConfig.java` | 显式设置 UTF-8 字符集 |
| `LzljPaymentChannelServiceImpl.java` | 改为全量同步策略 |
| `LzljMerchantServiceImpl.java` | 改为全量同步策略 |
| `XxlJobConfig.java` | 新增 XXL-JOB 配置 |
| `SaasSyncJobHandler.java` | 新增同步任务处理器 |
| `account-lzlj-user.yml` | 新增 XXL-JOB Nacos 配置 |

### 数据库迁移

执行 `sql/migrations/021_alter_lzlj_payment_channel_fields.sql` 变更表结构。

## Related

- 原 OpenSpec: `lzlj-merchant-management`
