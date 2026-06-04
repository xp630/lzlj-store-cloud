-- ============================================
-- SaaS 用户角色关联表设置 id 自增
-- ============================================

ALTER TABLE `saas_auth_user_role` MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID';
