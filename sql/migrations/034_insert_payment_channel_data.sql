-- ============================================
-- 初始化支付渠道数据
-- ============================================
INSERT INTO `saas_auth_payment_channel` (`channel_code`, `channel_name`, `status`) VALUES
('WECHAT_PAY', '微信支付', 1),
('ALIPAY', '支付宝', 1),
('UNION_PAY', '银联支付', 1);
