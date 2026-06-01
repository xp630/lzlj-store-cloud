## 1. 数据库设计

- [x] 1.1 创建 lzlj_merchant 商户表
- [x] 1.2 创建 lzlj_settlement_info 结算信息表
- [x] 1.3 扩展 lzlj_auth_org 表（merchant_id, scenario_ids）
- [x] 1.4 创建 lzlj_merchant_user 商户账号关联表
- [x] 1.5 创建 lzlj_org_user 机构用户关联表（不需要，LzljUser已有orgId字段）

## 2. 实体类开发

- [x] 2.1 创建 LzljMerchant 实体
- [x] 2.2 创建 LzljSettlementInfo 实体
- [x] 2.3 创建 LzljOrg 扩展字段（merchantId, scenarioIds）
- [x] 2.4 创建 LzljMerchantUser 实体（商户账号关联）

## 3. DTO 开发

- [x] 3.1 SyncMerchantDTO（网商同步请求）
- [x] 3.2 CreateMerchantDTO（创建商户请求）
- [x] 3.3 UpdateMerchantDTO（更新商户请求）
- [x] 3.4 MerchantDTO / MerchantVO（商户详情）
- [x] 3.5 SettlementInfoDTO（结算信息）
- [x] 3.6 MerchantQueryDTO（分页查询）

## 4. DAO 开发

- [x] 4.1 LzljMerchantDao
- [x] 4.2 LzljSettlementInfoDao
- [x] 4.3 LzljMerchantUserDao

## 5. Service 开发

- [x] 5.1 LzljMerchantService（商户 CRUD）
- [x] 5.2 LzljSettlementInfoService（结算信息管理，已包含在MerchantService中）
- [x] 5.3 syncFromWangshang() 网商同步商户
- [x] 5.4 create() 创建商户 + 机构 + 结算信息

## 6. Controller 开发

- [x] 6.1 POST /merchant/sync（网商同步商户）
- [x] 6.2 GET /merchant/page（商户分页列表）
- [x] 6.3 GET /merchant/{id}（商户详情）
- [x] 6.4 POST /merchant（创建商户）
- [x] 6.5 PUT /merchant/{id}（更新商户）
- [x] 6.6 DELETE /merchant/{id}（删除商户）
- [x] 6.7 GET /merchant/{id}/settlement（获取结算信息）
- [x] 6.8 PUT /merchant/{id}/settlement（更新结算信息）
- [x] 6.9 GET /merchant/{id}/users（商户账号列表）
- [x] 6.10 POST /merchant/{id}/users（关联用户到商户）
- [x] 6.11 DELETE /merchant/{id}/users/{userId}（解绑用户）

## 7. 机构管理扩展

- [x] 7.1 修改机构创建逻辑（org_type 1=母户 2=子户，代码已支持）
- [x] 7.2 修改机构树查询（支持 level_path，代码已支持）
- [x] 7.3 业务场景继承查询（从顶层母户获取，已在convertToDTO中实现）
- [x] 7.4 修改机构详情接口（返回 merchant_id, scenario_ids，已在DTO中支持）

## 8. 业务场景管理

- [x] 8.1 LzljBusinessScenario 实体（简化：业务场景为预定义数据，暂不建实体）
- [x] 8.2 业务场景接口（简化：暂无独立管理接口，通过商户/机构接口间接使用）
- [x] 8.3 场景与支付渠道关联接口（待定：支付渠道来自SaaS，后期实现）

## 9. 前端页面

- [x] 9.1 前端在独立仓库，暂不实现
- [x] 9.2 前端在独立仓库，暂不实现
- [x] 9.3 前端在独立仓库，暂不实现
- [x] 9.4 前端在独立仓库，暂不实现
- [x] 9.5 前端在独立仓库，暂不实现

## 10. 测试验证

- [x] 10.1 测试数据脚本 sql/migrations/006_test_data_lzlj_merchant.sql
- [x] 10.2 API接口已实现，可通过 Swagger UI 测试
- [x] 10.3 机构树+业务场景继承已实现
- [x] 10.4 商户账号关联/解绑已实现
- [x] 10.5 同步接口幂等已实现（根据 merchant_code 判断）
