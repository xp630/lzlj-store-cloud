-- ============================================
-- SaaS 菜单管理表（平台级数据，无租户隔离）
-- ============================================

-- 菜单表
CREATE TABLE IF NOT EXISTS `saas_auth_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID（顶级为0）',
    `name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型（0:目录 1:菜单 2:按钮）',
    `permission` VARCHAR(100) DEFAULT NULL COMMENT '权限标识（如: system:user:list）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0:禁用 1:启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- 初始化平台级菜单
INSERT INTO `saas_auth_menu` (`id`, `parent_id`, `name`, `path`, `component`, `icon`, `sort`, `type`, `permission`, `status`)
VALUES
    (1, 0, '系统管理', '/system', NULL, 'Setting', 1, 0, NULL, 1),
    (2, 1, '租户管理', '/system/tenant', 'system/tenant/index', 'Team', 1, 1, 'system:tenant:list', 1),
    (3, 1, '用户管理', '/system/user', 'system/user/index', 'User', 2, 1, 'system:user:list', 1),
    (4, 1, '菜单管理', '/system/menu', 'system/menu/index', 'Menu', 3, 1, 'system:menu:list', 1),
    (5, 1, '角色管理', '/system/role', 'system/role/index', 'Role', 4, 1, 'system:role:list', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
