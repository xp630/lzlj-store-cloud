package com.lzlj.account.role.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.role.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 角色菜单关联 Mapper
 */
@Mapper
public interface RoleMenuDao extends BaseMapper<RoleMenu> {

    /**
     * 硬删除角色的所有菜单关联（不走 @TableLogic 软删）
     * 用于 assignMenus / deleteRole 场景：先删再插，唯一键 (role_id, menu_id) 不允许重复
     */
    @Update("DELETE FROM saas_auth_role_menu WHERE role_id = #{roleId}")
    int deleteByRoleIdHard(@Param("roleId") Long roleId);
}
