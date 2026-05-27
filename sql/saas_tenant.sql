-- ============================================
-- SaaS 租户管理表
-- ============================================

-- 租户表
CREATE TABLE IF NOT EXISTS `saas_auth_tenant` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `tenant_code` VARCHAR(50) NOT NULL COMMENT '租户编码（唯一）',
    `tenant_name` VARCHAR(100) NOT NULL COMMENT '租户名称',
    `tenant_desc` VARCHAR(500) DEFAULT NULL COMMENT '租户描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `contact` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
    `package_id` BIGINT DEFAULT NULL COMMENT '套餐ID',
    `user_limit` INT DEFAULT NULL COMMENT '用户数量上限',
    `logo` VARCHAR(255) DEFAULT NULL COMMENT 'logo地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_code` (`tenant_code`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租户表';

-- 初始化超级管理员租户（平台级租户，tenant_id = 0）
INSERT INTO `saas_auth_tenant` (`id`, `tenant_code`, `tenant_name`, `tenant_desc`, `status`, `create_time`)
VALUES (0, 'platform', '平台管理', '系统内置平台管理租户', 1, NOW())
ON DUPLICATE KEY UPDATE `tenant_name` = VALUES(`tenant_name`);
