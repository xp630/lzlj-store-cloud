## Why

SaaS 商户需要支持多种支付渠道（微信支付、支付宝、银行转账等），每个商户可以独立开通不同的支付渠道，并且每个渠道可以配置不同的手续费率。目前系统没有商户渠道概念，无法满足多渠道支付接入的业务需求。

## What Changes

1. **新增商户渠道表** (`saas_auth_merchant_channel`) - 存储商户与渠道的开通关系
2. **新增渠道费率表** (`saas_auth_channel_rate`) - 存储每个渠道的配置费率
3. **新增渠道字典表** (`saas_auth_payment_channel`) - 存储支持的支付渠道类型（微信、支付宝等）
4. **新增商户渠道管理 API** - 商户可查询、开通、关闭支付渠道
5. **新增渠道费率管理 API** - 商户可为每个渠道设置不同的费率
6. **商户创建/更新时同步渠道信息** - 渠道配置作为商户的附属信息

## Capabilities

### New Capabilities
- `merchant-channel`: 商户渠道管理 - 管理商户与支付渠道的开通关系、渠道状态、费率配置
- `channel-rate`: 渠道费率管理 - 统一管理各支付渠道的费率模板和实际费率

### Modified Capabilities
- `merchant`: 商户管理 - 已有商户功能，本次新增渠道配置子功能，不修改原有接口

## Impact

- **新增表**: `saas_auth_merchant_channel`, `saas_auth_channel_rate`, `saas_auth_payment_channel`
- **受影响模块**: `cloud-account-saas-biz-auth` (商户模块)
- **新增 API**:
  - `GET /internal/merchant-channels/{merchantId}` - 获取商户已开通渠道
  - `POST /internal/merchant-channels` - 开通商户渠道
  - `PUT /internal/merchant-channels/{id}` - 更新渠道状态/费率
  - `DELETE /internal/merchant-channels/{id}` - 关闭渠道
  - `GET /payment-channels` - 获取可用支付渠道列表
  - `POST /channel-rates` - 创建费率配置
  - `PUT /channel-rates/{id}` - 更新费率配置
