-- ============================================
-- LZLJ Cloud 数据库初始化脚本
-- JDK 17 + Spring Cloud Alibaba 2023
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS lzlj_cloud DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lzlj_cloud;

-- ============================================
-- 1. 用户模块表
-- ============================================

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
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

-- 组织表（经销商/门店）
CREATE TABLE IF NOT EXISTS `sys_org` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `org_code` VARCHAR(50) NOT NULL COMMENT '组织编码',
    `org_name` VARCHAR(100) NOT NULL COMMENT '组织名称',
    `org_type` TINYINT NOT NULL COMMENT '组织类型 1:总代理 2:省代 3:市代 4:门店',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父级ID',
    `level_path` VARCHAR(500) DEFAULT NULL COMMENT '层级路径',
    `level` INT NOT NULL DEFAULT 1 COMMENT '层级深度',
    `province_code` VARCHAR(20) DEFAULT NULL COMMENT '省代码',
    `city_code` VARCHAR(20) DEFAULT NULL COMMENT '市代码',
    `district_code` VARCHAR(20) DEFAULT NULL COMMENT '区代码',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
    `contact` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `tenant_id` BIGINT DEFAULT NULL COMMENT '租户ID',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_org_code` (`org_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_org_type` (`org_type`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织表';

-- 菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父级ID',
    `path` VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `menu_type` TINYINT NOT NULL DEFAULT 1 COMMENT '菜单类型 1:目录 2:菜单 3:按钮',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '显示状态 0:隐藏 1:显示',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    `perms` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `data_scope` TINYINT NOT NULL DEFAULT 1 COMMENT '数据范围 1:全部 2:本级 3:本级及以下 4:自定义',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `tenant_id` BIGINT DEFAULT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ============================================
-- 2. 商品模块表
-- ============================================

-- 商品分类表
CREATE TABLE IF NOT EXISTS `pms_category` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父级ID',
    `level` INT NOT NULL DEFAULT 1 COMMENT '层级',
    `level_path` VARCHAR(500) DEFAULT NULL COMMENT '层级路径',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `tenant_id` BIGINT DEFAULT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `pms_goods` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `goods_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `goods_code` VARCHAR(100) NOT NULL COMMENT '商品编码',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `brand_id` BIGINT DEFAULT NULL COMMENT '品牌ID',
    `spu_code` VARCHAR(100) DEFAULT NULL COMMENT 'SPU编码',
    `sku_code` VARCHAR(100) DEFAULT NULL COMMENT 'SKU编码',
    `price` DECIMAL(10,2) NOT NULL COMMENT '销售价格',
    `cost_price` DECIMAL(10,2) DEFAULT NULL COMMENT '成本价',
    `market_price` DECIMAL(10,2) DEFAULT NULL COMMENT '市场价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `stock_alarm` INT DEFAULT NULL COMMENT '库存预警值',
    `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
    `weight` DECIMAL(10,2) DEFAULT NULL COMMENT '重量(kg)',
    `pic` VARCHAR(255) DEFAULT NULL COMMENT '主图',
    `pics` TEXT COMMENT '图片列表(JSON)',
    `description` TEXT COMMENT '商品描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0:下架 1:上架',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `tenant_id` BIGINT DEFAULT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_goods_code` (`goods_code`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================
-- 3. 订单模块表
-- ============================================

-- 订单表
CREATE TABLE IF NOT EXISTS `oms_order` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `org_id` BIGINT NOT NULL COMMENT '门店ID',
    `order_type` TINYINT NOT NULL DEFAULT 1 COMMENT '订单类型 1:普通订单 2:秒杀订单 3:预售订单',
    `total_amount` DECIMAL(12,2) NOT NULL COMMENT '订单总额',
    `pay_amount` DECIMAL(12,2) NOT NULL COMMENT '应付金额',
    `freight_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '运费金额',
    `discount_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    `pay_type` TINYINT DEFAULT NULL COMMENT '支付方式 1:微信 2:支付宝 3:银行卡',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '订单状态 1:待付款 2:待发货 3:已发货 4:已完成 5:已取消 6:退款中 7:已退款',
    `receiver_name` VARCHAR(50) DEFAULT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货人电话',
    `receiver_province` VARCHAR(50) DEFAULT NULL COMMENT '收货省',
    `receiver_city` VARCHAR(50) DEFAULT NULL COMMENT '收货市',
    `receiver_district` VARCHAR(50) DEFAULT NULL COMMENT '收货区',
    `receiver_address` VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
    `delivery_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `receive_time` DATETIME DEFAULT NULL COMMENT '收货时间',
    `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
    `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS `oms_order_item` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `goods_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `goods_code` VARCHAR(100) NOT NULL COMMENT '商品编码',
    `pic` VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `sub_total` DECIMAL(12,2) NOT NULL COMMENT '小计金额',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入超级管理员用户 (密码: admin123)
INSERT INTO `sys_user` (`id`, `username`, `password`, `salt`, `real_name`, `phone`, `email`, `status`, `user_type`, `tenant_id`, `create_time`)
VALUES (1, 'admin', 'd7a0045f7b8c9e8c9e8c9e8c9e8c9e8c9', 'adminSalt', '系统管理员', '13800138000', 'admin@lzlj.com', 1, 1, 1, NOW());

-- 插入组织数据
INSERT INTO `sys_org` (`id`, `org_code`, `org_name`, `org_type`, `parent_id`, `level`, `level_path`, `status`, `sort`, `tenant_id`, `create_time`)
VALUES
(1, 'HQ', '泸州老窖总部', 1, 0, 1, '/1/', 1, 0, 1, NOW()),
(2, 'PROV_001', '四川省代', 2, 1, 2, '/1/2/', 1, 1, 1, NOW()),
(3, 'CITY_001', '成都市代', 3, 2, 3, '/1/2/3/', 1, 1, 1, NOW()),
(4, 'SHOP_001', '成都旗舰店', 4, 3, 4, '/1/2/3/4/', 1, 1, 1, NOW());
