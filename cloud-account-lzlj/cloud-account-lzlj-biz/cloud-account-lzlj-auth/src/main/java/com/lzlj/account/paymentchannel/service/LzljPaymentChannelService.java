package com.lzlj.account.paymentchannel.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.paymentchannel.dto.LzljPaymentChannelDTO;
import com.lzlj.account.paymentchannel.dto.LzljPaymentChannelQueryDTO;

import java.util.List;

/**
 * LZLJ 支付通道服务接口
 */
public interface LzljPaymentChannelService {

    /**
     * 获取支付通道详情
     */
    LzljPaymentChannelDTO getById(Long id);

    /**
     * 支付通道分页列表
     */
    PageResult<LzljPaymentChannelDTO> page(LzljPaymentChannelQueryDTO query);

    /**
     * 获取所有启用的支付通道
     */
    List<LzljPaymentChannelDTO> listEnabled();

    /**
     * 同步支付通道（从网商等外部系统同步）
     */
    void syncFromExternal();
}
