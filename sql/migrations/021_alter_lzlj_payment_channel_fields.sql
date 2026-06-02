-- ============================================
-- LZLJ 支付通道表字段变更（与 SaaS 保持一致）
-- ============================================
-- 移除：channel_type, description, fee_rate, min_amount, max_amount
-- 新增：cloud_account_fee, upstream_cost_fee, total_fee_cost, per_transaction_limit

ALTER TABLE `lzlj_auth_payment_channel`
    DROP COLUMN IF EXISTS `channel_type`,
    DROP COLUMN IF EXISTS `description`,
    DROP COLUMN IF EXISTS `fee_rate`,
    DROP COLUMN IF EXISTS `min_amount`,
    DROP COLUMN IF EXISTS `max_amount`,
    ADD COLUMN IF NOT EXISTS `cloud_account_fee` DECIMAL(10,6) COMMENT '云账户管理费率' AFTER `payment_method`,
    ADD COLUMN IF NOT EXISTS `upstream_cost_fee` DECIMAL(10,6) COMMENT '上游成本费率' AFTER `cloud_account_fee`,
    ADD COLUMN IF NOT EXISTS `total_fee_cost` DECIMAL(10,6) COMMENT '总费率成本（技术服务费）' AFTER `upstream_cost_fee`,
    ADD COLUMN IF NOT EXISTS `per_transaction_limit` DECIMAL(12,2) COMMENT '单笔限额' AFTER `total_fee_cost`;
