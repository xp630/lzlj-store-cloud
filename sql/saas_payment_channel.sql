-- ============================================
-- SaaS 支付通道管理表（平台级数据，无租户隔离）
-- ============================================

CREATE TABLE IF NOT EXISTS `saas_auth_payment_channel` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `channel_code` VARCHAR(50) NOT NULL COMMENT '通道编码（UNIONPAY/银联, NETBANK/网商）',
    `channel_name` VARCHAR(50) NOT NULL COMMENT '通道名称（银联/网商）',
    `payment_method` VARCHAR(200) DEFAULT NULL COMMENT '支付方式（逗号分隔，如 WECHAT,ALIPAY）',
    `cloud_account_fee` DECIMAL(10,4) DEFAULT NULL COMMENT '云账户管理费率',
    `upstream_cost_fee` DECIMAL(10,4) DEFAULT NULL COMMENT '上游成本费率',
    `total_fee_cost` DECIMAL(10,4) DEFAULT NULL COMMENT '总费率成本（技术服务费）',
    `per_transaction_limit` DECIMAL(12,2) DEFAULT NULL COMMENT '单笔限额',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_channel_code` (`channel_code`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付通道管理表';
