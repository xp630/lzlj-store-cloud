-- ============================================
-- 商户渠道关系表
-- ============================================
CREATE TABLE `saas_auth_merchant_channel` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
    `channel_id` BIGINT NOT NULL COMMENT '渠道ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '开通状态(0关闭 1开通)',
    `rate_type` TINYINT NOT NULL DEFAULT 1 COMMENT '费率类型(1固定费率)',
    `rate_value` DECIMAL(10,4) NOT NULL DEFAULT 0 COMMENT '费率值(如0.006表示0.6%)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开通时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_merchant` (`tenant_id`, `merchant_id`),
    KEY `idx_channel_id` (`channel_id`),
    UNIQUE KEY `uk_merchant_channel` (`merchant_id`, `channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户渠道关系表';
