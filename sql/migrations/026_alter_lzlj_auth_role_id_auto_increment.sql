-- ============================================
-- LZLJ 角色表 ID 改为自增
-- ============================================
ALTER TABLE `lzlj_auth_role` MODIFY COLUMN `id` BIGINT NOT NULL AUTO_INCREMENT FIRST;
