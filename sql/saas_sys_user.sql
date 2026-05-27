-- ============================================
-- SaaS 用户表初始化
-- ============================================

-- 用户表
CREATE TABLE IF NOT EXISTS `saas_auth_user` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `salt` VARCHAR(20) NOT NULL COMMENT '盐值',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0:未知 1:男 2:女',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用 2:锁定',
    `user_type` TINYINT NOT NULL DEFAULT 3 COMMENT '用户类型 1:超级管理员 2:管理员 3:普通用户',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `last_login_time` BIGINT DEFAULT NULL COMMENT '最后登录时间',
    `wx_openid` VARCHAR(100) DEFAULT NULL COMMENT '微信OpenID',
    `wx_ma_openid` VARCHAR(100) DEFAULT NULL COMMENT '微信小程序OpenID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `tenant_id` BIGINT DEFAULT NULL COMMENT '租户ID',
    `org_id` BIGINT DEFAULT NULL COMMENT '组织ID',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试用户 (密码是 123456，用MD5加密)
-- 密码: 123456, salt: test1234
-- 加密后: md5("123456" + "test1234") = 89f2a8c77d6b9dae2c4f4f4f4f4f4f4f
INSERT INTO `saas_auth_user` (`id`, `username`, `password`, `salt`, `real_name`, `phone`, `email`, `status`, `user_type`, `tenant_id`, `org_id`, `create_time`)
VALUES (1, 'admin', '89f2a8c77d6b9dae2c4f4f4f4f4f4f4f', 'test1234', '管理员', '13800138000', 'admin@example.com', 1, 1, 0, NULL, NOW())
ON DUPLICATE KEY UPDATE `real_name` = '管理员';

-- 插入租户A的测试用户
INSERT INTO `saas_auth_user` (`id`, `username`, `password`, `salt`, `real_name`, `phone`, `email`, `status`, `user_type`, `tenant_id`, `org_id`, `create_time`)
VALUES (2, 'tenant_a_admin', '89f2a8c77d6b9dae2c4f4f4f4f4f4f4f', 'test1234', '租户A管理员', '13800138001', 'tenant_a@example.com', 1, 1, 1, NULL, NOW())
ON DUPLICATE KEY UPDATE `real_name` = '租户A管理员';

-- 插入租户B的测试用户
INSERT INTO `saas_auth_user` (`id`, `username`, `password`, `salt`, `real_name`, `phone`, `email`, `status`, `user_type`, `tenant_id`, `org_id`, `create_time`)
VALUES (3, 'tenant_b_admin', '89f2a8c77d6b9dae2c4f4f4f4f4f4f4f', 'test1234', '租户B管理员', '13800138002', 'tenant_b@example.com', 1, 1, 2, NULL, NOW())
ON DUPLICATE KEY UPDATE `real_name` = '租户B管理员';
