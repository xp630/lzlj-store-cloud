package com.lzlj.account.tenant.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.tenant.entity.AdminTenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 管理员租户关联 Mapper
 */
@Mapper
public interface AdminTenantDao extends BaseMapper<AdminTenant> {

    /**
     * 硬删除用户的所有租户关联（不走 @TableLogic 软删）
     */
    @Update("DELETE FROM saas_auth_admin_tenant WHERE user_id = #{adminUserId}")
    void deleteByUserIdHard(@Param("adminUserId") Long adminUserId);
}
