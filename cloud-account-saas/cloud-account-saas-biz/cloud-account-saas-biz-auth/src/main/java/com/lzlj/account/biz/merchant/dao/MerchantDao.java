package com.lzlj.account.biz.merchant.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.biz.merchant.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户 Mapper
 */
@Mapper
public interface MerchantDao extends BaseMapper<Merchant> {
}
