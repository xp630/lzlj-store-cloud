## 1. Database Schema

- [x] 1.1 Create `saas_auth_payment_channel` table (渠道字典表)
- [x] 1.2 Create `saas_auth_merchant_channel` table (商户渠道关系表)
- [x] 1.3 Create migration SQL files in `sql/migrations/`
- [x] 1.4 Insert initial channel data (WECHAT_PAY, ALIPAY, UNION_PAY)

## 2. Entity & DTO

- [x] 2.1 Create `PaymentChannel` entity class
- [x] 2.2 Create `MerchantChannel` entity class
- [x] 2.3 Create `PaymentChannelDTO` for channel dictionary API
- [x] 2.4 Create `MerchantChannelDTO` for merchant channel API
- [x] 2.5 Create `CreateMerchantChannelDTO` for开通请求
- [x] 2.6 Create `UpdateMerchantChannelDTO` for费率更新请求

## 3. DAO Layer

- [x] 3.1 Create `PaymentChannelDao` interface
- [x] 3.2 Create `MerchantChannelDao` interface
- [x] 3.3 Add MyBatis-Plus query methods for each DAO

## 4. Service Layer

- [x] 4.1 Create `PaymentChannelService` interface
- [x] 4.2 Create `PaymentChannelServiceImpl` implementation
- [x] 4.3 Create `MerchantChannelService` interface
- [x] 4.4 Create `MerchantChannelServiceImpl` implementation
- [x] 4.5 Implement channel开通 logic with duplicate check
- [x] 4.6 Implement商户渠道列表查询 with channel details join
- [x] 4.7 Implement费率更新逻辑

## 5. Controller Layer

- [x] 5.1 Create `InternalMerchantChannelController` for商户渠道管理
- [x] 5.2 Add `POST /internal/merchant-channels` 开通渠道接口
- [x] 5.3 Add `GET /internal/merchant-channels/{merchantId}` 渠道列表接口
- [x] 5.4 Add `PUT /internal/merchant-channels/{id}` 更新渠道状态/费率
- [x] 5.5 Add `DELETE /internal/merchant-channels/{id}` 关闭渠道
- [x] 5.6 Add `GET /payment-channels` 获取可用渠道列表

## 6. Integration

- [x] 6.1 Add `List<MerchantChannelDTO>` to `MerchantDTO` for商户详情返回
- [x] 6.2 Update `SaaSMerchantServiceImpl` to include channel info
- [x] 6.3 Verify tenant isolation for channel queries (由 TenantEntity 框架自动处理)
