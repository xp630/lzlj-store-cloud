-- ============================================
-- 商户表移除法人信息字段（已迁移到 lzlj_auth_merchant_legal）
-- ============================================
ALTER TABLE `lzlj_auth_merchant`
    DROP COLUMN `license_no`,
    DROP COLUMN `legal_person`;
