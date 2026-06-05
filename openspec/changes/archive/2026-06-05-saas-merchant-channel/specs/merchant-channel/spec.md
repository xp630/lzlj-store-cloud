## ADDED Requirements

### Requirement: 商户渠道开通
系统 SHALL 支持商户开通支付渠道。商户通过商户ID和渠道ID发起开通请求，系统创建商户渠道关系记录，初始状态为已开通。

#### Scenario: 成功开通渠道
- **WHEN** 商户发起开通支付渠道请求（merchantId=1, channelId=1）
- **THEN** 系统创建商户渠道记录，状态为开通(1)，返回记录ID

#### Scenario: 重复开通同一渠道
- **WHEN** 商户再次开通已开通的渠道
- **THEN** 系统返回错误: 该渠道已开通

### Requirement: 商户渠道关闭
系统 SHALL 支持商户关闭已开通的支付渠道。关闭后渠道状态变为禁用，不再参与支付路由。

#### Scenario: 成功关闭渠道
- **WHEN** 商户发起关闭渠道请求（merchantChannelId=1）
- **THEN** 系统更新渠道状态为关闭(0)

### Requirement: 商户渠道列表查询
系统 SHALL 支持查询商户已开通的所有渠道列表，返回渠道信息、状态、费率。

#### Scenario: 查询商户渠道列表
- **WHEN** 查询商户已开通渠道（merchantId=1）
- **THEN** 返回该商户所有已开通渠道的详细信息，包含渠道名称、状态、费率

#### Scenario: 查询未开通任何渠道的商户
- **WHEN** 查询商户渠道列表，但该商户未开通任何渠道
- **THEN** 返回空列表

### Requirement: 商户渠道费率更新
系统 SHALL 支持更新商户渠道的费率配置。

#### Scenario: 更新费率成功
- **WHEN** 更新商户渠道费率（merchantChannelId=1, rateValue=0.007）
- **THEN** 系统更新费率值

### Requirement: 渠道字典查询
系统 SHALL 支持查询所有可用的支付渠道列表，供开通时选择。

#### Scenario: 查询可用渠道列表
- **WHEN** 查询所有可用支付渠道
- **THEN** 返回状态为启用的渠道列表（渠道编码、名称）

### Requirement: 商户渠道详情查询
系统 SHALL 支持根据ID查询商户渠道详情。

#### Scenario: 查询商户渠道详情
- **WHEN** 查询商户渠道详情（merchantChannelId=1）
- **THEN** 返回渠道详细信息，包含渠道编码、名称、状态、费率、开通时间
