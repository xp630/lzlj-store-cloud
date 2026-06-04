-- ============================================
-- SaaS 管理员租户关联表设置 id 自增
-- ============================================

ALTER TABLE `saas_auth_admin_tenant` MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID';
