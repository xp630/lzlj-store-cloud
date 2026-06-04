-- ============================================
-- 菜单表增加选中图标字段
-- ============================================
-- SaaS
ALTER TABLE `saas_auth_menu` ADD COLUMN `selected_icon` VARCHAR(128) COMMENT '选中图标' AFTER `icon`;
-- LZLJ
ALTER TABLE `lzlj_auth_menu` ADD COLUMN `selected_icon` VARCHAR(128) COMMENT '选中图标' AFTER `icon`;
