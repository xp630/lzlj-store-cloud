-- 创建数据角色表
CREATE TABLE `lzlj_auth_data_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `data_role_name` varchar(100) NOT NULL COMMENT '数据角色名称',
  `data_role_code` varchar(100) NOT NULL COMMENT '数据角色编码',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0:禁用 1:启用',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0:未删除 1:已删除',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_data_role_code` (`data_role_code`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据角色表';
