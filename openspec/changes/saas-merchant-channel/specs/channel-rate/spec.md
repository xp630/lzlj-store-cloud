## ADDED Requirements

### Requirement: 渠道字典管理
系统 SHALL 提供支付渠道字典的增删改查能力。渠道字典是系统级配置，供所有商户选择开通。

#### Scenario: 查询所有渠道字典
- **WHEN** 查询所有支付渠道字典
- **THEN** 返回渠道编码、名称、状态列表

#### Scenario: 根据编码查询渠道
- **WHEN** 根据渠道编码查询（如 WECHAT_PAY）
- **THEN** 返回该渠道的详细信息

### Requirement: 渠道状态管理
系统 SHALL 支持启用/禁用支付渠道。禁用后商户无法再开通该渠道，但不影响已开通的渠道。

#### Scenario: 禁用渠道
- **WHEN** 禁用支付渠道（channelId=1）
- **THEN** 渠道状态变为禁用(0)

#### Scenario: 启用渠道
- **WHEN** 启用支付渠道（channelId=1）
- **THEN** 渠道状态变为启用(1)

### Requirement: 固定费率配置
系统 SHALL 支持为商户渠道配置固定费率。费率值以小数形式存储（如0.006表示0.6%）。

#### Scenario: 商户渠道设置固定费率
- **WHEN** 为商户渠道设置固定费率（merchantChannelId=1, rateValue=0.006）
- **THEN** 系统存储费率类型为固定费率(1)，费率为0.006

#### Scenario: 费率精度
- **WHEN** 设置费率为0.006
- **THEN** 系统存储精确值0.006，不四舍五入

### Requirement: 费率查询
系统 SHALL 支持查询商户渠道的当前费率配置。

#### Scenario: 查询商户渠道费率
- **WHEN** 查询商户渠道费率（merchantChannelId=1）
- **THEN** 返回费率类型和费率值
