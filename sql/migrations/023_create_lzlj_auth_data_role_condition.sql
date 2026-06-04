-- 创建数据角色条件表
CREATE TABLE `lzlj_auth_data_role_condition` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `data_role_id` bigint NOT NULL COMMENT '数据角色ID',
  `field_name` varchar(100) NOT NULL COMMENT '字段名',
  `operator` varchar(20) NOT NULL COMMENT '操作符: =, !=, >, <, >=, <=, IN, LIKE, BETWEEN',
  `value_type` tinyint NOT NULL DEFAULT '1' COMMENT '值类型: 1:固定值 2:动态值',
  `field_value` varchar(500) DEFAULT NULL COMMENT '固定值',
  `dynamic_value_key` varchar(100) DEFAULT NULL COMMENT '动态值key: currentUser.orgId, currentUser.userId',
  `logical_operator` varchar(10) NOT NULL DEFAULT 'AND' COMMENT '逻辑操作符: AND, OR',
  `condition_group` int NOT NULL DEFAULT '1' COMMENT '条件分组编号',
  `sort` int NOT NULL DEFAULT '1' COMMENT '排序',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 0:未删除 1:已删除',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_data_role_id` (`data_role_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据角色条件表';
