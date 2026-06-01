-- ============================================
-- 扩展商户表：增加商户类型和网商账号
-- ============================================
USE lzlj_account;

-- 添加备注字段（BaseEntity 需要）
ALTER TABLE `lzlj_auth_merchant`
    ADD COLUMN `remark` VARCHAR(500) COMMENT '备注' AFTER `version`;

-- 添加商户类型和网商账号
ALTER TABLE `lzlj_auth_merchant`
    ADD COLUMN `merchant_type` TINYINT NOT NULL DEFAULT 1 COMMENT '商户类型 1:母户 2:子户' AFTER `account_status`,
    ADD COLUMN `wangshang_account` VARCHAR(64) COMMENT '网商商户账号' AFTER `merchant_type`;
