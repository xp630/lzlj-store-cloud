## Why

LZLJ 系统需要完整的商户管理体系。商户在网商系统开户后，需要在 LZLJ 创建对应的销售组织结构，并支持：
- 商户主数据管理（企业信息、法人信息、结算信息）
- 商户与销售机构的关联
- 业务场景配置（控制支付渠道可见性）
- 商户账号管理（用户与商户的关联）

## What Changes

### 新增实体

| 实体 | 说明 |
|------|------|
| LzljMerchant | 商户主数据表 |
| LzljSettlementInfo | 结算信息表（1:1 关联商户） |
| LzljOrg | 机构表扩展（增加 merchant_id, scenario_ids） |

### 修改实体

| 实体 | 修改内容 |
|------|----------|
| LzljOrg | 增加 parent_id, level_path, level 字段支持树结构；增加 merchant_id 关联商户；增加 scenario_ids 配置业务场景 |

### 业务流程

1. **创建商户** → 同步创建母户机构 + 结算信息 + 默认账号
2. **创建子户** → 手动在机构树下创建，自动继承母户业务场景
3. **业务场景继承** → 用户查询时通过 level_path 找到顶层母户获取场景

## Capabilities

### New Capabilities

- `lzlj-merchant-crud`: 商户主数据增删改查（企业信息、法人信息、结算信息）
- `lzlj-merchant-org-sync`: 商户创建时自动同步创建顶层机构（母户）
- `lzlj-settlement-info`: 结算信息管理（对公/对私结算账户）
- `lzlj-business-scenario`: 业务场景配置（商户级别，母户统一配置）

### Modified Capabilities

- `lzlj-org-tree`: 机构树增加商户关联和业务场景字段

## Impact

**新增模块：** lzlj-merchant

**受影响模块：**
- lzlj-auth: 机构管理扩展、用户与商户关联
- lzlj-gateway: 商户相关 API 路由

**外部依赖：**
- 网商系统（商户数据来源）
- SaaS 系统（支付渠道数据，远程调用）
