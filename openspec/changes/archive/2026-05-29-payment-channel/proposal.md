## Why

支付通道是泸州老窖云店系统的支付基础设施配置模块。当前系统缺少统一的支付通道管理能力，需要为前端提供可配置的支付渠道选项，并为后续支付流程集成（支付宝、微信支付等）提供配置基础。

## What Changes

- 新增支付通道管理模块（SAAS only，平台级配置）
- 支持支付通道的 CRUD 和分页查询
- 通道编码和通道名称存两个字段，前端无需翻译
- 支付方式（微信/支付宝/银行卡/云闪付/POS机）通过枚举配置，逗号分隔存储
- 通道枚举（渠道）：银联(UNIONPAY)、网商(NETBANK)
- 支付方式枚举：微信(WECHAT)、支付宝(ALIPAY)、银行卡(BANK_CARD)、云闪付(QUICK_PASS)、POS机(POS)
- 费率信息（管理费率、成本费率、总费率、单笔限额）可配置
- 与"支付场景"模块无关，支付场景已废弃

## Capabilities

### New Capabilities

- `payment-channel`: 支付通道管理模块，支持通道的增删改查分页，包含费率配置

## Impact

- 新增模块：`cloud-account-saas-biz/cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/payment-channel/`
- 新增数据库表：`saas_auth_payment_channel`
- 新增枚举类：`com.lzlj.account.common.core.enums.PaymentChannelEnum` (渠道枚举)
- 新增枚举类：`com.lzlj.account.common.core.enums.PaymentMethodEnum` (支付方式枚举)
- 受影响服务：`saas-auth` (9092)
- 依赖：无外部依赖
