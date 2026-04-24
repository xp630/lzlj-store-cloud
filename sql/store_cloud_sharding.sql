-- ============================================
-- store_cloud 分库分表脚本 (最简化版 - 2表)
-- ============================================

CREATE DATABASE IF NOT EXISTS `store_cloud` DEFAULT CHARACTER SET utf8mb4;
USE store_cloud;

-- ============================================
-- 用户表分片 (sys_user_0, sys_user_1)
-- 分片键: id % 2
-- ============================================

CREATE TABLE IF NOT EXISTS `sys_user_0` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `salt` VARCHAR(20) NOT NULL COMMENT '盐值',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `gender` TINYINT DEFAULT 0 COMMENT '性别 0:未知 1:男 2:女',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    `user_type` TINYINT NOT NULL DEFAULT 3 COMMENT '用户类型',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `last_login_time` BIGINT DEFAULT NULL COMMENT '最后登录时间',
    `wx_openid` VARCHAR(100) DEFAULT NULL COMMENT '微信OpenID',
    `wx_ma_openid` VARCHAR(100) DEFAULT NULL COMMENT '微信小程序OpenID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `tenant_id` BIGINT DEFAULT NULL COMMENT '租户ID',
    `org_id` BIGINT DEFAULT NULL COMMENT '组织ID',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表_0';

CREATE TABLE IF NOT EXISTS `sys_user_1` LIKE `sys_user_0`;

-- ============================================
-- 商品表分片 (pms_goods_0, pms_goods_1)
-- 分片键: create_by % 2
-- ============================================

CREATE TABLE IF NOT EXISTS `pms_goods_0` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `goods_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `goods_code` VARCHAR(100) NOT NULL COMMENT '商品编码',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `pic` VARCHAR(255) DEFAULT NULL COMMENT '主图',
    `description` TEXT COMMENT '描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID(分片键)',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `tenant_id` BIGINT DEFAULT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`),
    KEY `idx_create_by` (`create_by`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表_0';

CREATE TABLE IF NOT EXISTS `pms_goods_1` LIKE `pms_goods_0`;

-- ============================================
-- 测试数据
-- ============================================

-- 用户 (id=1 -> 1%2=1 -> sys_user_1)
INSERT INTO `sys_user_1` (`id`, `username`, `password`, `salt`, `real_name`, `phone`, `email`, `status`, `user_type`, `tenant_id`, `org_id`)
VALUES (1, 'admin', 'd7a0045f7b8c9e8c9e8c9e8c9e8c9e8c9', 'adminSalt', '系统管理员', '13800138000', 'admin@lzlj.com', 1, 1, 1, 1);

-- 用户 (id=2 -> 2%2=0 -> sys_user_0)
INSERT INTO `sys_user_0` (`id`, `username`, `password`, `salt`, `real_name`, `phone`, `email`, `status`, `user_type`, `tenant_id`, `org_id`)
VALUES (2, 'user2', 'd7a0045f7b8c9e8c9e8c9e8c9e8c9e8c9', 'salt', '用户2', '13800138002', 'user2@lzlj.com', 1, 3, 1, 2);

-- 商品 (creator_id=1 -> 1%2=1 -> pms_goods_1)
INSERT INTO `pms_goods_1` (`id`, `goods_name`, `goods_code`, `category_id`, `price`, `stock`, `unit`, `status`, `create_by`, `tenant_id`)
VALUES (1, '商品-1', 'G001', 1, 15.00, 110, '件', 1, 1, 1);

-- 商品 (creator_id=2 -> 2%2=0 -> pms_goods_0)
INSERT INTO `pms_goods_0` (`id`, `goods_name`, `goods_code`, `category_id`, `price`, `stock`, `unit`, `status`, `create_by`, `tenant_id`)
VALUES (2, '商品-2', 'G002', 1, 20.00, 100, '件', 1, 2, 1);
