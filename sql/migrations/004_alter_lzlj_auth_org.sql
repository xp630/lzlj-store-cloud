-- ============================================
-- 扩展机构表：增加商户关联和业务场景
-- ============================================
USE lzlj_cloud;

-- 新增字段
ALTER TABLE `lzlj_auth_org`
    ADD COLUMN `merchant_id` BIGINT COMMENT '关联商户ID' AFTER `sort`,
    ADD COLUMN `scenario_ids` JSON COMMENT '业务场景ID列表' AFTER `merchant_id`;

-- 注意：org_type 含义变更
-- 原：1总代理 2省代 3市代 4门店
-- 新：1=母户 2=子户
-- 数据迁移需要手动处理，或者保持原样通过业务逻辑区分

-- ============================================
-- 扩展商户表：增加开户状态
-- ============================================
ALTER TABLE `lzlj_merchant`
    ADD COLUMN `account_status` TINYINT NOT NULL DEFAULT 0 COMMENT '开户状态 0未开户 1开户中 2已开户 3开户失败' AFTER `status`;
