-- ============================================
-- SaaS 系统参数管理表（平台级数据，无租户隔离）
-- ============================================

CREATE TABLE IF NOT EXISTS `saas_auth_system_parameter` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `param_key` VARCHAR(100) NOT NULL COMMENT '参数编码',
    `param_name` VARCHAR(100) NOT NULL COMMENT '参数名称',
    `param_value` VARCHAR(500) NOT NULL COMMENT '参数值',
    `param_type` VARCHAR(20) DEFAULT 'STRING' COMMENT '参数类型（STRING/INTEGER/BOOLEAN/DECIMAL）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_param_key` (`param_key`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统参数表';
