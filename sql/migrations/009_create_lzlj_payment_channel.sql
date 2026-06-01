-- ============================================
-- LZLJ 支付通道表（平台级数据，无租户隔离）
-- ============================================
CREATE TABLE IF NOT EXISTS `lzlj_auth_payment_channel` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `channel_code` VARCHAR(64) NOT NULL COMMENT '通道编码，如 WECHAT/ALIPAY/NETBANK',
    `channel_name` VARCHAR(128) NOT NULL COMMENT '通道名称',
    `channel_type` VARCHAR(32) COMMENT '通道类型，如 WECHAT/ALIPAY/UNIONPAY/NETBANK',
    `payment_method` VARCHAR(64) COMMENT '支付方式，如 WECHAT/ALIPAY/FASTPAY',
    `description` VARCHAR(500) COMMENT '描述',
    `fee_rate` DECIMAL(10,6) COMMENT '费率',
    `min_amount` DECIMAL(12,2) COMMENT '最低交易金额',
    `max_amount` DECIMAL(12,2) COMMENT '最高交易金额',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_channel_code` (`channel_code`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='LZLJ支付通道表';
