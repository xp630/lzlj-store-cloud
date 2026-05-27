package com.lzlj.account.role.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.role.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色 Mapper
 */
@Mapper
public interface RoleDao extends BaseMapper<Role> {
}
