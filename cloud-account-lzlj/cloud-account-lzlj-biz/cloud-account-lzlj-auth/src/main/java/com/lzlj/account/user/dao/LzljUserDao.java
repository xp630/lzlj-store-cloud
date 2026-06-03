package com.lzlj.account.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.lzlj.account.user.entity.LzljUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * LZLJ 用户Mapper
 */
@Mapper
public interface LzljUserDao extends BaseMapper<LzljUser> {

    /**
     * 根据用户名查询（管理员登录用，忽略租户）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM lzlj_auth_user WHERE username = #{username} AND deleted = 0 LIMIT 1")
    LzljUser selectByUsernameWithoutTenant(@Param("username") String username);

    /**
     * 根据手机号查询（用户登录用，忽略租户）
     */
    @InterceptorIgnore(tenantLine = "true")
    @Select("SELECT * FROM lzlj_auth_user WHERE phone = #{phone} AND deleted = 0 LIMIT 1")
    LzljUser selectByPhoneWithoutTenant(@Param("phone") String phone);
}
