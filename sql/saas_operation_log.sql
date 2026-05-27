-- ============================================
-- SaaS 操作日志表
-- ============================================

-- 操作日志表
CREATE TABLE IF NOT EXISTS `saas_auth_operation_log` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    `module` VARCHAR(50) NOT NULL COMMENT '模块（如：user, role, menu）',
    `operation` VARCHAR(50) NOT NULL COMMENT '操作类型（如：CREATE, UPDATE, DELETE）',
    `content` VARCHAR(500) DEFAULT NULL COMMENT '操作内容',
    `biz_id` BIGINT DEFAULT NULL COMMENT '业务ID（如：被操作的记录ID）',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '浏览器UA',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_module` (`module`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
