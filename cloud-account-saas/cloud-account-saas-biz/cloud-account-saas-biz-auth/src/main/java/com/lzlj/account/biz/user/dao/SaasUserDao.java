package com.lzlj.account.biz.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.lzlj.account.biz.user.entity.SaasUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper
 */
@Mapper
public interface SaasUserDao extends BaseMapper<SaasUser> {

    /**
     * 根据用户名查询（超级管理员登录用，忽略租户）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM saas_auth_user WHERE username = #{username} AND deleted = 0 LIMIT 1")
    SaasUser selectByUsernameWithoutTenant(@Param("username") String username);

    /**
     * 根据手机号查询（普通用户登录用，忽略租户）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM saas_auth_user WHERE phone = #{phone} AND deleted = 0 LIMIT 1")
    SaasUser selectByPhoneWithoutTenant(@Param("phone") String phone);


    /**
     * 根据id查询
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM saas_auth_user WHERE id = #{id} AND deleted = 0 LIMIT 1")
    SaasUser selectByIdWithoutTenant(@Param("id") Long id);
}
