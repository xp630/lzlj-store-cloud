package com.lzlj.account.biz.merchantchannel.service;

import com.lzlj.account.biz.merchantchannel.dto.CreateMerchantChannelDTO;
import com.lzlj.account.biz.merchantchannel.dto.MerchantChannelDTO;
import com.lzlj.account.biz.merchantchannel.dto.UpdateMerchantChannelDTO;

import java.util.List;

/**
 * 商户渠道服务接口
 */
public interface SaasMerchantChannelService {

    /**
     * 开通商户渠道
     */
    Long create(CreateMerchantChannelDTO dto);

    /**
     * 更新商户渠道
     */
    void update(Long id, UpdateMerchantChannelDTO dto);

    /**
     * 关闭商户渠道
     */
    void delete(Long id);

    /**
     * 获取商户渠道详情
     */
    MerchantChannelDTO getById(Long id);

    /**
     * 获取商户所有渠道列表
     */
    List<MerchantChannelDTO> listByMerchantId(Long merchantId);
}
