-- ============================================
-- SaaS 用户角色关联表（平台级数据，无租户隔离）
-- 角色分配是平台级操作，所有租户共享同一套用户-角色映射关系
-- ============================================

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `saas_auth_user_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表（平台级，无租户隔离）';
