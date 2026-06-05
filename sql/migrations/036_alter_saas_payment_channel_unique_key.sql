-- ============================================
-- 修改 saas_auth_payment_channel 唯一约束
-- 从 channel_code 改为 channel_code + payment_method 组合唯一
-- ============================================

-- 1. 先删除旧的唯一键（如果有重复数据需先清理）
-- ALTER TABLE saas_auth_payment_channel DROP INDEX uk_channel_code;

-- 2. 添加新的组合唯一键
ALTER TABLE saas_auth_payment_channel DROP INDEX uk_channel_code;
ALTER TABLE saas_auth_payment_channel ADD UNIQUE KEY `uk_channel_code_payment_method` (`channel_code`, `payment_method`);
