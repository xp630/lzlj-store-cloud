-- ============================================
-- SaaS API密钥管理表
-- ============================================

-- API密钥表
CREATE TABLE IF NOT EXISTS `saas_auth_api_key` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `tenant_id` BIGINT NOT NULL COMMENT '绑定的租户ID',
    `api_key` VARCHAR(50) NOT NULL COMMENT 'API公钥（ak_开头）',
    `api_secret` VARCHAR(100) NOT NULL COMMENT 'API密钥（加密存储）',
    `name` VARCHAR(100) NOT NULL COMMENT 'API名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT 'API描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
    `rate_limit` INT DEFAULT 100 COMMENT '速率限制（次/分钟）',
    `expires_time` DATETIME DEFAULT NULL COMMENT '过期时间（NULL表示永不过期）',
    `last_used_time` DATETIME DEFAULT NULL COMMENT '最后使用时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_api_key` (`api_key`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API密钥表';
