-- 修改 lzlj_auth_org 表：将 scenario_ids 改为 scenario_id (Long类型)
-- 业务场景ID从JSON数组改为单个ID

-- 重命名列并修改类型（MySQL 5.7+）
ALTER TABLE lzlj_auth_org
CHANGE COLUMN scenario_ids scenario_id BIGINT DEFAULT NULL COMMENT '业务场景ID';
