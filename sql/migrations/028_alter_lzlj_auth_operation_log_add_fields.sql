-- ============================================
-- LZLJ 操作日志表新增字段
-- ============================================

-- 新增组织名称字段
ALTER TABLE `lzlj_auth_operation_log` ADD COLUMN `org_name` VARCHAR(100) DEFAULT NULL COMMENT '组织名称' AFTER `org_id`;

-- 新增功能角色字段（角色名称逗号分隔）
ALTER TABLE `lzlj_auth_operation_log` ADD COLUMN `functional_roles` VARCHAR(500) DEFAULT NULL COMMENT '功能角色列表（逗号分隔）' AFTER `org_name`;

-- 新增数据角色字段（角色名称逗号分隔）
ALTER TABLE `lzlj_auth_operation_log` ADD COLUMN `data_roles` VARCHAR(500) DEFAULT NULL COMMENT '数据角色列表（逗号分隔）' AFTER `functional_roles`;
