-- ----------------------------
-- 商户表
-- ----------------------------
DROP TABLE IF EXISTS `saas_merchant_merchant`;
CREATE TABLE `saas_merchant_merchant` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户ID',
  `org_id` bigint DEFAULT NULL COMMENT '组织ID',
  `merchant_code` varchar(64) NOT NULL COMMENT '商户编码（唯一）',
  `merchant_name` varchar(128) NOT NULL COMMENT '商户名称',
  `short_name` varchar(64) DEFAULT NULL COMMENT '商户简称',
  `contact` varchar(64) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(128) DEFAULT NULL COMMENT '联系邮箱',
  `address` varchar(256) DEFAULT NULL COMMENT '联系地址',
  `license_no` varchar(64) DEFAULT NULL COMMENT '营业执照号',
  `legal_person` varchar(64) DEFAULT NULL COMMENT '法人代表',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态 0:禁用 1:启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `version` int DEFAULT NULL COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_merchant_code` (`merchant_code`, `deleted`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商户表';
