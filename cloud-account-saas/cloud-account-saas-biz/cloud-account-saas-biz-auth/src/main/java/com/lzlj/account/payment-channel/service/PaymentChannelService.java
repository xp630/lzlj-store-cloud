package com.lzlj.account.paymentchannel.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.paymentchannel.dto.CreatePaymentChannelDTO;
import com.lzlj.account.paymentchannel.dto.PaymentChannelDTO;
import com.lzlj.account.paymentchannel.dto.PaymentChannelQueryDTO;
import com.lzlj.account.paymentchannel.dto.UpdatePaymentChannelDTO;

import java.util.List;

/**
 * 支付通道服务接口
 */
public interface PaymentChannelService {

    /**
     * 创建支付通道
     */
    Long create(CreatePaymentChannelDTO dto);

    /**
     * 更新支付通道
     */
    void update(Long id, UpdatePaymentChannelDTO dto);

    /**
     * 删除支付通道
     */
    void delete(Long id);

    /**
     * 获取支付通道详情
     */
    PaymentChannelDTO getById(Long id);

    /**
     * 分页查询支付通道
     */
    PageResult<PaymentChannelDTO> page(PaymentChannelQueryDTO query, Integer pageNum, Integer pageSize);

    /**
     * 获取支付通道列表
     */
    List<PaymentChannelDTO> list();
}
