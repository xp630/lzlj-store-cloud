package com.lzlj.account.biz.paychannel.service;

import com.lzlj.account.biz.paychannel.dto.CreatePaymentChannelDTO;
import com.lzlj.account.biz.paychannel.dto.PaymentChannelDTO;
import com.lzlj.account.biz.paychannel.dto.PaymentChannelQueryDTO;
import com.lzlj.account.biz.paychannel.dto.UpdatePaymentChannelDTO;
import com.lzlj.account.common.core.domain.PageResult;


import java.util.List;

/**
 * 支付通道服务接口
 */
public interface SaasPaymentChannelService {

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
