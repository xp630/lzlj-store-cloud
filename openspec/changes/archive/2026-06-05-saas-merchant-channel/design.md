## Context

SaaS 系统需要支持商户多渠道支付接入。每个商户可以开通多个支付渠道（微信支付、支付宝、银联等），每个渠道可以配置不同的手续费率。商户发起支付时，系统根据开通的渠道和费率进行路由和计费。

### 现状
- 商户表 `saas_auth_merchant` 已存在，包含商户基本信息
- 无渠道概念，无法区分不同支付通道

### 约束
- 渠道配置数据量小，适合与商户一起管理
- 费率需要支持多种计费模式（固定费率、阶梯费率）
- SaaS 多商户场景，渠道数据按租户隔离

## Goals / Non-Goals

**Goals:**
- 商户可开通/关闭支付渠道
- 每个渠道可配置不同的费率（支持固定费率模式）
- 渠道信息可查询，供支付时路由使用

**Non-Goals:**
- 不包含支付通道的具体接入实现（仅管理配置）
- 不包含对账、清结算功能
- 不支持阶梯费率（首期仅支持固定费率）

## Decisions

### 1. 数据模型设计

**支付渠道字典表** (`saas_auth_payment_channel`)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| channel_code | VARCHAR(32) | 渠道编码( WECHAT_PAY/ALIPAY/UNION_PAY) |
| channel_name | VARCHAR(64) | 渠道名称 |
| status | TINYINT | 状态(0禁用1启用) |

**商户渠道表** (`saas_auth_merchant_channel`)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| merchant_id | BIGINT | 商户ID |
| channel_id | BIGINT | 渠道ID(关联payment_channel) |
| status | TINYINT | 开通状态(0关闭1开通) |
| rate_type | TINYINT | 费率类型(1固定费率) |
| rate_value | DECIMAL(10,4) | 费率值(如0.006表示0.6%) |
| created_time | DATETIME | 开通时间 |

**设计理由**: 渠道信息与商户解耦，通过中间表实现多对多关系，支持同一渠道被多个商户开通。

### 2. 表结构命名
- 表名前缀: `saas_auth_`
- 渠道字典: `saas_auth_payment_channel`
- 商户渠道关系: `saas_auth_merchant_channel`
- 不新增费率表，费率字段直接存储在商户渠道关系表中

### 3. API 路径设计
- 内部接口前缀: `/internal/merchant-channels`
- 渠道字典: `/payment-channels`
- 遵循现有 REST 风格

## Risks / Trade-offs

[Risk] 费率字段直接存储在商户渠道表，扩展性受限
→ [Mitigation] 首期需求简单，先存储在关系表。后续如需复杂费率逻辑，可独立出费率表

[Risk] 多商户场景下渠道数据隔离
→ [Mitigation] 所有查询默认按 tenantId 隔离，内部接口通过 X-Tenant-Id header 传递

[Risk] 渠道字典数据初始化
→ [Mitigation] 提供初始化脚本，插入微信/支付宝/银联等基础渠道数据
