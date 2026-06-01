## Context

LZLJ 是泸州老窖云店系统，需要管理商户及其销售组织结构。商户在网商系统开户后，需要在 LZLJ 创建对应的销售组织（机构树），并配置业务场景来控制支付渠道的可见性。

## Goals / Non-Goals

**Goals:**
- 商户主数据管理（CRUD）
- 商户与结算信息分离（独立表）
- 商户创建时自动同步创建顶层机构（母户）
- 机构树支持母户-子户层级结构
- 业务场景配置在母户上，子户继承

**Non-Goals:**
- 不实现商户入驻审核流程
- 不实现结算信息审核/对账
- 不实现与网商的实时双向同步（初期单向拉取）

## Decisions

### 1. 数据模型设计

#### LzljMerchant 商户主数据表

```sql
CREATE TABLE lzlj_merchant (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_code   VARCHAR(64) NOT NULL UNIQUE COMMENT '商户编号 M-001',
    merchant_name   VARCHAR(128) NOT NULL COMMENT '商户全称',
    short_name      VARCHAR(64) COMMENT '商户简称',
    contact         VARCHAR(64) COMMENT '联系人',
    contact_phone   VARCHAR(32) COMMENT '联系电话',
    contact_email   VARCHAR(128) COMMENT '联系邮箱',
    province_code   VARCHAR(20) COMMENT '省代码',
    city_code       VARCHAR(20) COMMENT '市代码',
    district_code   VARCHAR(20) COMMENT '区代码',
    address         VARCHAR(256) COMMENT '详细地址',
    license_no      VARCHAR(64) COMMENT '营业执照号',
    legal_person    VARCHAR(64) COMMENT '法人代表',
    status          TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time     DATETIME,
    update_time     DATETIME,
    deleted         TINYINT NOT NULL DEFAULT 0,
    version         INT DEFAULT 0,
    UNIQUE KEY uk_merchant_code (merchant_code, deleted)
) COMMENT '商户主数据表';
```

#### LzljSettlementInfo 结算信息表

```sql
CREATE TABLE lzlj_settlement_info (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    merchant_id         BIGINT NOT NULL UNIQUE COMMENT '商户ID 1:1',
    settlement_type     TINYINT NOT NULL DEFAULT 1 COMMENT '结算类型 1对公 2对私',
    bank_name           VARCHAR(128) COMMENT '开户行名称',
    bank_branch_name    VARCHAR(128) COMMENT '开户行支行',
    bank_account        VARCHAR(64) COMMENT '银行账号',
    account_name        VARCHAR(128) COMMENT '开户名称',
    settlement_cycle    VARCHAR(32) COMMENT '结算周期 T+N',
    status              TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time         DATETIME,
    update_time         DATETIME,
    deleted             TINYINT NOT NULL DEFAULT 0,
    version             INT DEFAULT 0,
    UNIQUE KEY uk_merchant_id (merchant_id, deleted)
) COMMENT '结算信息表';
```

#### LzljOrg 机构表扩展

```sql
-- 新增字段（扩展现有 lzlj_auth_org 表）
ALTER TABLE lzlj_auth_org
    ADD COLUMN merchant_id BIGINT COMMENT '关联商户ID',
    ADD COLUMN scenario_ids JSON COMMENT '业务场景ID列表',
    ADD COLUMN parent_id BIGINT COMMENT '父机构ID',
    ADD COLUMN level_path VARCHAR(255) COMMENT '层级路径 /1/2/3/',
    ADD COLUMN level INT COMMENT '层级深度';

-- org_type 重构：1=母户 2=子户（原来 1总代 2省代 3市代 4门店 废除）
```

### 2. 商户创建流程

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   网商系统    │         │   LZLJ API   │         │   LZLJ DB    │
└──────┬───────┘         └──────┬───────┘         └──────┬───────┘
       │                        │                        │
       │ 1. 创建商户             │                        │
       │───────────────────────>│                        │
       │                        │ 2. 事务处理             │
       │                        │   - INSERT merchant    │
       │                        │   - INSERT settlement  │
       │                        │   - INSERT org(母户)  │
       │                        │   - INSERT user(默认)  │
       │                        │   - INSERT user_org    │
       │                        │────────────────────────>│
       │                        │                        │
       │ 3. 返回结果             │                        │
       │<───────────────────────│                        │
```

**同步接口：**

```java
@PostMapping("/merchant/sync")
public Result<MerchantSyncVO> syncMerchant(@RequestBody SyncMerchantDTO dto);
```

**幂等处理：** 根据 merchant_code 判断是否已存在，已存在则更新。

### 3. 机构树结构

```
泸州老窖 (母户, org_type=1)
    │
    ├── 成都大区 (子户, org_type=2)
    │     ├── 锦江区店 (子户)
    │     └── 龙泉驿区店 (子户)
    │
    └── 重庆大区 (子户, org_type=2)
          ├── 渝中区店 (子户)
          └── 南岸区店 (子户)

商户 (Merchant) ←─── 1:1 ───→ 母户 (Org)
```

**业务场景继承逻辑：**

```sql
-- 用户属于子机构 org_id=101，查询业务场景
-- Step 1: 从 level_path 提取顶层 org_id
SELECT parent_id FROM lzlj_auth_org WHERE id = 101;
-- 结果: parent_id = 1 (母户ID)

-- Step 2: 获取顶层母户的业务场景
SELECT scenario_ids FROM lzlj_auth_org WHERE id = 1;
-- 结果: [1, 2, 3]
```

### 4. 商户账号关联

```
LzljUser ─── N:1 ───> LzljOrg ─── 1:1 ───> LzljMerchant
  │                                                      │
  └── user_org 关联表 (一个用户可属于多个机构？)              │
                                                              │
LzljMerchantUser (商户账号关联表) ◄── N:1 ──────────────────┘
  │ merchant_id, user_id
  │ 角色: 门店管理员/店员/收银员...
```

## Risks / Trade-offs

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 商户与机构同步失败 | 数据不一致 | 事务保证；补偿机制 |
| 业务场景修改影响下级 | 误操作风险 | 确认弹窗；操作日志 |
| 结算信息敏感字段 | 数据安全 | 加密存储；脱敏展示 |
| SaaS 支付渠道调用失败 | 渠道不可用 | 本地缓存 + 降级策略 |
