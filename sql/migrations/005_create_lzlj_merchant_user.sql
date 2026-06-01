-- ============================================
-- 商户账号关联表（用户与商户的多对多关联）
-- ============================================
CREATE TABLE IF NOT EXISTS `lzlj_auth_merchant_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `merchant_id` BIGINT NOT NULL COMMENT '商户ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(32) COMMENT '角色：merchant_admin门店管理员/clerk店员/cashier收银员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
    PRIMARY KEY (`id`),
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商户账号关联表';
