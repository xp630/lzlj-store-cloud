-- ============================================
-- SaaS API访问日志表
-- ============================================

-- API访问日志表
CREATE TABLE IF NOT EXISTS `saas_auth_api_log` (
    `id` BIGINT NOT NULL COMMENT '主键ID',
    `api_key_id` BIGINT NOT NULL COMMENT 'API密钥ID',
    `api_key` VARCHAR(50) NOT NULL COMMENT 'API公钥',
    `tenant_id` BIGINT NOT NULL COMMENT '租户ID',
    `method` VARCHAR(10) NOT NULL COMMENT 'HTTP方法',
    `path` VARCHAR(200) NOT NULL COMMENT '请求路径',
    `request_body` TEXT DEFAULT NULL COMMENT '请求体',
    `response_body` TEXT DEFAULT NULL COMMENT '响应体',
    `status_code` INT NOT NULL COMMENT 'HTTP状态码',
    `duration` INT NOT NULL COMMENT '响应耗时（毫秒）',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '客户端IP',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '客户端UA',
    `error_msg` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '请求时间',
    PRIMARY KEY (`id`),
    KEY `idx_api_key_id` (`api_key_id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_path` (`path`),
    KEY `idx_status_code` (`status_code`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API访问日志表';
