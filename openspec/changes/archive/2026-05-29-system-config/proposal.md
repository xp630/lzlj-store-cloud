## Why

SaaS 和 LZLJ 系统目前缺少统一的系统参数管理和数据字典功能。系统参数用于存储全局配置（如分页大小、密码策略），数据字典用于存储可枚举的业务数据（如支付状态、订单状态）。这两个功能是系统基础能力，被多个业务模块依赖。

## What Changes

为 SaaS 和 LZLJ 两个系统分别添加：
1. **系统参数管理 (SystemParameter)** - 平台级配置，key-value 模式
2. **系统数据字典 (DataDictionary)** - 平台级配置，扁平结构按 type 分组

### 系统参数管理
- 参数编码（key）- 全局唯一
- 参数值（value）
- 参数名称
- 参数类型（STRING/INTEGER/BOOLEAN/DECIMAL）
- 状态（启用/禁用）
- 备注

### 系统数据字典
- 字典编码 - 全局唯一
- 字典类型（分组，如：payment_status、order_status）
- 字典标签（显示名称）
- 字典值（存储值）
- 排序
- 状态（启用/禁用）
- 备注

## Capabilities

### New Capabilities
- `system-parameter`: 系统参数管理（增删改查）
- `data-dictionary`: 数据字典管理（增删改查，按 type 分组查询）

## Impact

- **SaaS**: `cloud-account-saas-biz-auth` 模块新增 SystemParameter、DataDictionary 实体和服务
- **LZLJ**: `cloud-account-lzlj-biz-auth` 模块新增 LzljSystemParameter、LzljDataDictionary 实体和服务
- **Common**: `cloud-account-common-core` 新增通用枚举
- **数据库**: 新增 `saas_auth_system_parameter`、`saas_auth_data_dictionary`、`lzlj_auth_system_parameter`、`lzlj_auth_data_dictionary` 表
- **平台级**: 两个功能均继承 BaseEntity，无租户/机构隔离
