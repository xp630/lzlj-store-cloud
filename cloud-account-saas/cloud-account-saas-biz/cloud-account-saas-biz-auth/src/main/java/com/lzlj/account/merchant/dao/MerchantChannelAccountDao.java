package com.lzlj.account.merchant.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.merchant.entity.MerchantChannelAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商户银联账户信息Mapper
 */
@Mapper
public interface MerchantChannelAccountDao extends BaseMapper<MerchantChannelAccount> {
}
