-- ============================================
-- SaaS 商户表增加字段
-- ============================================
ALTER TABLE `saas_merchant_merchant`
    ADD COLUMN `wangshang_account` VARCHAR(64) COMMENT '支付渠道账号' AFTER `address`;
