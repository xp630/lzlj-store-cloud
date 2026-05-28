package com.lzlj.account.role.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.role.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联 Mapper
 */
@Mapper
public interface RoleMenuDao extends BaseMapper<RoleMenu> {
}
