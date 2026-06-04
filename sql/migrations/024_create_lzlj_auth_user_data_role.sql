-- 创建用户数据角色关联表
CREATE TABLE `lzlj_auth_user_data_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `data_role_id` bigint NOT NULL COMMENT '数据角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_data_role_id` (`data_role_id`),
  UNIQUE KEY `uk_user_data_role` (`user_id`, `data_role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户数据角色关联表';
