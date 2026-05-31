package com.lzlj.account.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.user.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 用户角色关联 Mapper
 */
@Mapper
public interface UserRoleDao extends BaseMapper<UserRole> {

    /**
     * 硬删除用户的所有角色关联（不走 @TableLogic 软删）
     * 用于 assignRoles 场景：先删再插，唯一键 (user_id, role_id) 不允许重复
     */
    @Update("DELETE FROM saas_auth_user_role WHERE user_id = #{userId}")
    int deleteByUserIdHard(@Param("userId") Long userId);
}
