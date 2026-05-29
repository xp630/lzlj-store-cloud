## Context

泸州老窖云店系统需要支付通道管理模块，用于配置可用的支付渠道及其费率信息。该模块属于 SAAS 平台的支付基础设施，为后续支付流程集成提供配置基础。

当前状态：
- 无支付通道管理功能
- 支付场景模块已废弃，不做参考
- 系统已有 Menu/Role/User 等模块的分层结构和 CRUD 模式可参考

## Goals / Non-Goals

**Goals:**
- 实现支付通道的增删改查分页
- 支持费率配置（云账户管理费率、上游成本费率、总费率成本、单笔限额）
- 通道编码和名称独立存储，前端无需翻译
- 支付方式多选，通过枚举固定，逗号分隔存储
- 平台级配置，所有租户共享

**Non-Goals:**
- 不实现实际支付流程（不调用支付宝/微信API）
- 不实现支付通道与租户的绑定关系
- 不实现费率计算或结算功能

## Decisions

### 1. 继承 BaseEntity（非 TenantEntity）

**Decision**: PaymentChannel 继承 `BaseEntity`，不使用租户隔离。

**Rationale**: 支付通道是平台级配置，所有租户共享同一套通道列表。Menu/Role 等模块也使用 BaseEntity。

**Alternatives**:
- TenantEntity: 不合适，通道配置不属于单个租户

### 2. 两个枚举：渠道 + 支付方式

**Decision**: 定义两个枚举：
- `PaymentChannelEnum` (渠道): UNIONPAY(银联), NETBANK(网商)
- `PaymentMethodEnum` (支付方式): WECHAT(微信支付), ALIPAY(支付宝), BANK_CARD(银行卡), QUICK_PASS(云闪付), POS(POS机)

**Rationale**: 渠道和支付方式是两个不同维度，通过 paymentMethod 字段关联，支持多支付方式。

### 3. 通道编码和名称独立存储

**Decision**: 数据库存储 `channel_code` (UNIONPAY/NETBANK) 和 `channel_name` (银联/网商) 两个字段。

**Rationale**: 前端无需翻译，直接显示通道名称。

### 4. 支付方式逗号分隔存储

**Decision**: `payment_method` 字段存储枚举 codes，逗号分隔，如 `"WECHAT,ALIPAY"`

**Rationale**: 简单直接，避免 JSON 解析开销。

### 5. 费率字段手动输入

**Decision**: 云账户管理费率、上游成本费率、总费率成本、单笔限额均为手动输入字段。

**Rationale**: 费率由运营人员配置，不做自动计算逻辑。

## Risks / Trade-offs

- **风险**: 枚举通道类型未来可能需要扩展 → 缓解：新增枚举值即可
- **权衡**: 逗号分隔 vs JSON 数组 → 选择逗号分隔，更简单
