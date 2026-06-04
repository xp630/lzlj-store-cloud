-- ============================================
-- SaaS 商户表增加 经营类型、pid、开户状态字段
-- ============================================
ALTER TABLE `saas_auth_merchant`
    ADD COLUMN `business_type` TINYINT DEFAULT 1 COMMENT '经营类型 1:个人 2:企业经营 3:个体经营' AFTER `merchant_type`,
    ADD COLUMN `pid` BIGINT COMMENT '母商户ID' AFTER `business_type`,
    ADD COLUMN `account_status` TINYINT DEFAULT 0 COMMENT '开户状态 0:未开户 1:开户中 2:已开户 3:开户失败' AFTER `pid`;
