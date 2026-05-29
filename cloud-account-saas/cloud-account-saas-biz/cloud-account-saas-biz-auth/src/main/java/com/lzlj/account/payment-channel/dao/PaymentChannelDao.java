package com.lzlj.account.paymentchannel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.paymentchannel.entity.PaymentChannel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付通道 Mapper
 */
@Mapper
public interface PaymentChannelDao extends BaseMapper<PaymentChannel> {
}
