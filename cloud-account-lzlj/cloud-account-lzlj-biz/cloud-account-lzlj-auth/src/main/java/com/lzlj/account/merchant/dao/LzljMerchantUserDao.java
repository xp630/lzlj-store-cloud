package com.lzlj.account.merchant.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.merchant.entity.LzljMerchantUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * LZLJ 商户账号关联Mapper
 */
@Mapper
public interface LzljMerchantUserDao extends BaseMapper<LzljMerchantUser> {
}
