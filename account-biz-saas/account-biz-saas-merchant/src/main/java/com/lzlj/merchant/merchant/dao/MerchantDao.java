package com.lzlj.merchant.merchant.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.merchant.merchant.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户 Mapper
 */
@Mapper
public interface MerchantDao extends BaseMapper<Merchant> {
}
