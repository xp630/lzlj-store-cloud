-- ============================================
-- SaaS 管理员租户关联表
-- ============================================

-- 管理员租户关联表（支持一个管理员管理多个租户）
CREATE TABLE IF NOT EXISTS `saas_auth_admin_tenant` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `admin_user_id` BIGINT NOT NULL COMMENT '管理员用户ID',
    `tenant_id` BIGINT NOT NULL COMMENT '可管理的租户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_tenant` (`admin_user_id`, `tenant_id`),
    KEY `idx_admin_user_id` (`admin_user_id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员租户关联表';

-- 初始化：平台管理员 admin(id=1) 可管理平台租户(id=0)
INSERT INTO `saas_auth_admin_tenant` (`id`, `admin_user_id`, `tenant_id`)
VALUES (1, 1, 0)
ON DUPLICATE KEY UPDATE `admin_user_id` = VALUES(`admin_user_id`);
