package com.lzlj.account.merchant.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.merchant.entity.MerchantLegal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户法人信息Mapper
 */
@Mapper
public interface MerchantLegalDao extends BaseMapper<MerchantLegal> {
}
