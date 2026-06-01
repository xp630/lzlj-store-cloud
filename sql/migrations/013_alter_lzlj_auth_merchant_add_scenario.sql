-- ============================================
-- 商户表增加场景字段
-- ============================================
ALTER TABLE `lzlj_auth_merchant`
    ADD COLUMN `scenario_codes` JSON COMMENT '业务场景代码列表（母户用，JSON数组）' AFTER `wangshang_account`,
    ADD COLUMN `scenario_id` BIGINT COMMENT '业务场景ID（子户用）' AFTER `scenario_codes`;
