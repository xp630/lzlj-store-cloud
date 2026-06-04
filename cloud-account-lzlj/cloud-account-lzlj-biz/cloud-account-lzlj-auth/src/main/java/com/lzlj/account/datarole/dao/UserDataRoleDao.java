package com.lzlj.account.datarole.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.datarole.entity.UserDataRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据角色关联Mapper
 */
@Mapper
public interface UserDataRoleDao extends BaseMapper<UserDataRole> {
}
