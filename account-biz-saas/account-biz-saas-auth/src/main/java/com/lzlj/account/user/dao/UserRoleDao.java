package com.lzlj.account.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.user.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联 Mapper
 */
@Mapper
public interface UserRoleDao extends BaseMapper<UserRole> {
}
