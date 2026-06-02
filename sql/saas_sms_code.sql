-- ============================================
-- SaaS 短信验证码表
-- ============================================

-- 短信验证码表
CREATE TABLE IF NOT EXISTS `saas_auth_sms_code` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `code` VARCHAR(10) NOT NULL COMMENT '验证码',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: login/register/reset_pwd',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0未使用 1已使用 2已过期',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `used_at` DATETIME DEFAULT NULL COMMENT '使用时间',
    PRIMARY KEY (`id`),
    KEY `idx_phone` (`phone`),
    KEY `idx_code_phone_status` (`code`, `phone`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信验证码表';
