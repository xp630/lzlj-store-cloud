-- ============================================
-- Migration: 001_drop_user_role_tenant_id
-- Date: 2026-05-31
-- Description: 移除 saas_auth_user_role 表的 tenant_id 列
--              该列物理存在但逻辑上未被任何实体映射，是数据权限隐患
--              表本身是平台级数据，不需要租户隔离
-- ============================================

-- 1. 删除旧的唯一键（包含 tenant_id）
ALTER TABLE `saas_auth_user_role` DROP INDEX `uk_user_role`;

-- 2. 删除 tenant_id 列
ALTER TABLE `saas_auth_user_role` DROP COLUMN `tenant_id`;

-- 3. 删除 tenant_id 索引（如果存在）
ALTER TABLE `saas_auth_user_role` DROP INDEX `idx_tenant_id`;

-- 4. 添加新的唯一键（不含 tenant_id）
ALTER TABLE `saas_auth_user_role` ADD UNIQUE KEY `uk_user_role` (`user_id`, `role_id`);

-- 5. 更新表注释
ALTER TABLE `saas_auth_user_role` COMMENT = '用户角色关联表（平台级，无租户隔离）';
