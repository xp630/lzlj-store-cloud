package com.lzlj.account.biz.openapi.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.biz.openapi.entity.SaasApiKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * API密钥 Mapper
 */
@Mapper
public interface SaasApiKeyDao extends BaseMapper<SaasApiKey> {

    /**
     * 根据 API Key 查询（绕过租户拦截）
     * 用于 OpenAPI 签名验证时查询认证信息
     */
    @Select("SELECT * FROM saas_auth_api_key WHERE api_key = #{apiKey} AND status = 1 AND deleted = 0")
    SaasApiKey selectByApiKeyWithoutTenant(@Param("apiKey") String apiKey);
}
