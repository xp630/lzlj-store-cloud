-- ============================================
-- LZLJ 商户管理测试数据
-- 使用说明：执行前需先执行 002-005 的建表脚本
-- ============================================

USE lzlj_cloud;

-- ============================================
-- 1. 插入商户测试数据
-- ============================================
INSERT INTO `lzlj_auth_merchant` (`id`, `merchant_code`, `merchant_name`, `short_name`, `contact`, `contact_phone`, `contact_email`, `province_code`, `city_code`, `district_code`, `address`, `license_no`, `legal_person`, `status`, `merchant_type`, `wangshang_account`, `create_time`, `deleted`)
VALUES
(1, 'M-001', '泸州老窖股份有限公司', '泸州老窖', '张三', '13800138001', 'zhangsan@lzlj.com', '510000', '510100', '510104', '成都市锦江区xxx路', '91110000xxxx', '李四', 1, 1, 'WS001', NOW(), 0),
(2, 'M-002', '泸州老窖成都旗舰店', '成都旗舰店', '王五', '13800138002', 'wangwu@lzlj.com', '510000', '510100', '510105', '成都市武侯区xxx路', '91110000yyyy', '李四', 1, 2, 'WS002', NOW(), 0);

-- ============================================
-- 2. 插入结算信息测试数据
-- ============================================
INSERT INTO `lzlj_auth_merchant_settlement_info` (`id`, `merchant_id`, `settlement_type`, `bank_name`, `bank_branch_name`, `bank_account`, `account_name`, `settlement_cycle`, `status`, `create_time`, `deleted`)
VALUES
(1, 1, 1, '中国工商银行', '成都锦江支行', '6222021234567890', '泸州老窖股份有限公司', 'T+1', 1, NOW(), 0),
(2, 2, 2, '中国建设银行', '成都武侯支行', '6222021234567891', '泸州老窖成都旗舰店', 'T+3', 1, NOW(), 0);

-- ============================================
-- 3. 插入机构测试数据（母户+子户）
-- ============================================
INSERT INTO `lzlj_auth_org` (`id`, `org_code`, `org_name`, `org_type`, `parent_id`, `level_path`, `level`, `merchant_id`, `scenario_ids`, `province_code`, `city_code`, `district_code`, `address`, `contact`, `contact_phone`, `status`, `sort`, `create_time`, `deleted`)
VALUES
(1, 'ORG-M-001', '泸州老窖总部', 1, 0, '/1/', 1, 1, '[1,2,3,4,5,6]', '510000', '510100', '510104', '成都市锦江区xxx路', '张三', '13800138001', 1, 0, NOW(), 0),
(2, 'ORG-PROV-001', '四川省代理', 2, 1, '/1/2/', 2, NULL, NULL, '510000', '510100', NULL, NULL, NULL, NULL, 1, 1, NOW(), 0),
(3, 'ORG-CITY-001', '成都市代理', 3, 2, '/1/2/3/', 3, NULL, NULL, '510000', '510100', NULL, NULL, NULL, NULL, 1, 1, NOW(), 0),
(4, 'ORG-SHOP-001', '成都旗舰店', 4, 3, '/1/2/3/4/', 4, NULL, NULL, '510000', '510100', '510105', '成都市武侯区xxx路', '王五', '13800138002', 1, 1, NOW(), 0);

-- ============================================
-- 4. 插入商户用户关联测试数据
-- ============================================
INSERT INTO `lzlj_auth_merchant_user` (`id`, `merchant_id`, `user_id`, `role`, `status`, `create_time`, `deleted`)
VALUES
(1, 1, 1, 'merchant_admin', 1, NOW(), 0),
(2, 2, 2, 'clerk', 1, NOW(), 0);

-- ============================================
-- 5. 验证查询
-- ============================================

-- 查询1: 商户列表
-- SELECT * FROM lzlj_auth_merchant WHERE deleted = 0;

-- 查询2: 商户详情（含机构）
-- SELECT m.*, o.id as org_id, o.org_name as org_name
-- FROM lzlj_auth_merchant m
-- LEFT JOIN lzlj_auth_org o ON o.merchant_id = m.id AND o.org_type = 1 AND o.deleted = 0
-- WHERE m.deleted = 0;

-- 查询3: 机构树（含业务场景）
-- SELECT * FROM lzlj_auth_org WHERE deleted = 0 ORDER BY level_path;

-- 查询4: 用户所属机构的业务场景
-- SET @user_org_id = 4;  -- 旗舰店
-- SELECT scenario_ids FROM lzlj_auth_org WHERE id = (
--     SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(level_path, '/', 3), '/', -1) as top_org_id
--     FROM lzlj_auth_org WHERE id = @user_org_id
-- );

-- 查询5: 商户账号列表
-- SELECT mu.*, u.username, u.real_name, u.phone
-- FROM lzlj_auth_merchant_user mu
-- LEFT JOIN lzlj_auth_user u ON u.id = mu.user_id
-- WHERE mu.merchant_id = 1 AND mu.deleted = 0;
