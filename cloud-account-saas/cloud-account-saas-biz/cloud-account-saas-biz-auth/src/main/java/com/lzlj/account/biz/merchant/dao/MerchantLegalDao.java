package com.lzlj.account.biz.merchant.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.biz.merchant.entity.MerchantLegal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户法人信息Mapper
 */
@Mapper
public interface MerchantLegalDao extends BaseMapper<MerchantLegal> {
}
