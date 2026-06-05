package com.lzlj.account.biz.paychannel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.biz.paychannel.entity.SaasPaymentChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付通道 Mapper
 */
@Mapper
public interface SaasPaymentChannelDao extends BaseMapper<SaasPaymentChannel> {
}
