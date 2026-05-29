## 1. Database & Entity

- [x] 1.1 创建数据库表 SQL `saas_auth_payment_channel` (channel_code, channel_name, payment_method)
- [x] 1.2 创建 PaymentChannel 实体类，继承 BaseEntity
- [x] 1.3 创建 PaymentChannelEnum 枚举类（UNIONPAY/银联, NETBANK/网商）
- [x] 1.4 创建 PaymentMethodEnum 枚举类（WECHAT/ALIPAY/BANK_CARD/QUICK_PASS/POS）

## 2. DAO Layer

- [x] 2.1 创建 PaymentChannelDao 接口，继承 BaseMapper

## 3. DTO Layer

- [x] 3.1 创建 PaymentChannelDTO
- [x] 3.2 创建 CreatePaymentChannelDTO
- [x] 3.3 创建 UpdatePaymentChannelDTO
- [x] 3.4 创建 PaymentChannelQueryDTO（分页查询参数）

## 4. Service Layer

- [x] 4.1 创建 PaymentChannelService 接口
- [x] 4.2 实现 PaymentChannelServiceImpl，包含：
  - create() 创建通道
  - update() 更新通道
  - delete() 删除通道（软删除）
  - getById() 获取详情
  - getPage() 分页查询
  - getList() 列表查询

## 5. Controller Layer

- [x] 5.1 创建 PaymentChannelController，实现以下端点：
  - POST /payment-channel 创建
  - PUT /payment-channel/{id} 更新
  - DELETE /payment-channel/{id} 删除
  - GET /payment-channel/{id} 详情
  - GET /payment-channel/page 分页查询
  - GET /payment-channel/list 列表查询

## 6. 枚举类位置

- [x] 6.1 枚举类放在 common-core 项目：`com.lzlj.account.common.core.enums`
