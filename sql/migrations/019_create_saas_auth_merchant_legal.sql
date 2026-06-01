-- ============================================
-- SaaS 商户法人信息表
-- ============================================
CREATE TABLE IF NOT EXISTS `saas_auth_merchant_legal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
    `license_no` VARCHAR(64) COMMENT '营业执照号',
    `legal_person` VARCHAR(64) COMMENT '法人代表',
    `license_pic` VARCHAR(512) COMMENT '营业执照图片URL',
    `legal_id_card` VARCHAR(64) COMMENT '法人身份证号',
    `legal_id_card_pic` VARCHAR(512) COMMENT '法人身份证图片URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='SaaS商户法人信息表';
