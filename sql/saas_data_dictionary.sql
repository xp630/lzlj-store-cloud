-- ============================================
-- SaaS 数据字典表（平台级数据，无租户隔离）
-- ============================================

CREATE TABLE IF NOT EXISTS `saas_auth_data_dictionary` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `dict_code` VARCHAR(100) NOT NULL COMMENT '字典编码',
    `dict_type` VARCHAR(50) NOT NULL COMMENT '字典类型',
    `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
    `dict_value` VARCHAR(100) NOT NULL COMMENT '字典值',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_code`),
    KEY `idx_dict_type` (`dict_type`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典表';
