-- ============================================
-- 商户表（新结构）
-- ============================================
CREATE TABLE IF NOT EXISTS `lzlj_auth_merchant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_code` VARCHAR(64) NOT NULL COMMENT '商户编号 M-001',
    `merchant_name` VARCHAR(128) NOT NULL COMMENT '商户全称',
    `short_name` VARCHAR(64) COMMENT '商户简称',
    `contact` VARCHAR(64) COMMENT '联系人',
    `contact_phone` VARCHAR(32) COMMENT '联系电话',
    `contact_email` VARCHAR(128) COMMENT '联系邮箱',
    `province_code` VARCHAR(20) COMMENT '省代码',
    `city_code` VARCHAR(20) COMMENT '市代码',
    `district_code` VARCHAR(20) COMMENT '区代码',
    `address` VARCHAR(256) COMMENT '详细地址',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `account_status` TINYINT NOT NULL DEFAULT 0 COMMENT '开户状态 0未开户 1开户中 2已开户 3开户失败',
    `merchant_type` TINYINT NOT NULL DEFAULT 1 COMMENT '商户类型 1:母户 2:子户',
    `parent_id` BIGINT COMMENT '母商户ID（子户时必填）',
    `wangshang_account` VARCHAR(64) COMMENT '网商商户账号',
    `scenario_codes` JSON COMMENT '业务场景代码列表（母户用，JSON数组）',
    `scenario_id` BIGINT COMMENT '业务场景ID（子户用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_code` (`merchant_code`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商户主数据表';
