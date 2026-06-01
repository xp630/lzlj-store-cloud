package com.lzlj.account.paymentchannel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.paymentchannel.entity.LzljPaymentChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * LZLJ 支付通道Mapper
 */
@Mapper
public interface LzljPaymentChannelDao extends BaseMapper<LzljPaymentChannel> {
}
