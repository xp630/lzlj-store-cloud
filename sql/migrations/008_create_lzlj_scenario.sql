-- ============================================
-- 业务场景表
-- ============================================
CREATE TABLE IF NOT EXISTS `lzlj_auth_scenario` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_id` BIGINT NOT NULL COMMENT '关联母商户ID',
    `scenario_code` VARCHAR(64) NOT NULL COMMENT '场景代码，如 B2B/B2C/C2C',
    `scenario_name` VARCHAR(128) NOT NULL COMMENT '场景名称',
    `description` VARCHAR(500) COMMENT '描述',
    `icon` VARCHAR(255) COMMENT '图标',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_scenario` (`merchant_id`, `scenario_code`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='业务场景表';

-- ============================================
-- 业务场景与支付通道关联表
-- ============================================
CREATE TABLE IF NOT EXISTS `lzlj_auth_scenario_channel` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `scenario_id` BIGINT NOT NULL COMMENT '场景ID',
    `channel_id` BIGINT NOT NULL COMMENT '支付通道ID',
    `config` JSON COMMENT '通道配置JSON',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_scenario_channel` (`scenario_id`, `channel_id`, `deleted`),
    KEY `idx_scenario_id` (`scenario_id`),
    KEY `idx_channel_id` (`channel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='业务场景与支付通道关联表';
