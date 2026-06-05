package com.lzlj.account.biz.merchantchannel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.biz.paychannel.dao.SaasPaymentChannelDao;
import com.lzlj.account.biz.paychannel.entity.SaasPaymentChannel;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.biz.merchantchannel.dao.SaasMerchantChannelDao;
import com.lzlj.account.biz.merchantchannel.dto.CreateMerchantChannelDTO;
import com.lzlj.account.biz.merchantchannel.dto.MerchantChannelDTO;
import com.lzlj.account.biz.merchantchannel.dto.UpdateMerchantChannelDTO;
import com.lzlj.account.biz.merchantchannel.entity.SaasMerchantChannel;
import com.lzlj.account.biz.merchantchannel.service.SaasMerchantChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户渠道服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaasMerchantChannelServiceImpl implements SaasMerchantChannelService {

    private final SaasMerchantChannelDao merchantChannelDao;
    private final SaasPaymentChannelDao paymentChannelDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreateMerchantChannelDTO dto) {
        // 检查是否已开通该渠道
        LambdaQueryWrapper<SaasMerchantChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasMerchantChannel::getMerchantId, dto.getMerchantId())
               .eq(SaasMerchantChannel::getChannelId, dto.getChannelId());
        if (merchantChannelDao.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "该渠道已开通");
        }

        // 检查渠道是否存在
        SaasPaymentChannel channel = paymentChannelDao.selectById(dto.getChannelId());
        if (channel == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "渠道不存在");
        }

        SaasMerchantChannel merchantChannel = new SaasMerchantChannel();
        merchantChannel.setMerchantId(dto.getMerchantId());
        merchantChannel.setChannelId(dto.getChannelId());
        merchantChannel.setStatus(1); // 默认开通
        merchantChannel.setRateType(1); // 固定费率
        merchantChannel.setRateValue(dto.getRateValue() != null ? dto.getRateValue() : BigDecimal.ZERO);

        merchantChannelDao.insert(merchantChannel);
        return merchantChannel.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, UpdateMerchantChannelDTO dto) {
        SaasMerchantChannel merchantChannel = merchantChannelDao.selectById(id);
        if (merchantChannel == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (dto.getStatus() != null) {
            merchantChannel.setStatus(dto.getStatus());
        }
        if (dto.getRateValue() != null) {
            merchantChannel.setRateValue(dto.getRateValue());
        }

        merchantChannelDao.updateById(merchantChannel);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        merchantChannelDao.deleteById(id);
    }

    @Override
    public MerchantChannelDTO getById(Long id) {
        SaasMerchantChannel merchantChannel = merchantChannelDao.selectById(id);
        if (merchantChannel == null) {
            return null;
        }
        return convertToDTO(merchantChannel);
    }

    @Override
    public List<MerchantChannelDTO> listByMerchantId(Long merchantId) {
        LambdaQueryWrapper<SaasMerchantChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasMerchantChannel::getMerchantId, merchantId);
        List<SaasMerchantChannel> channels = merchantChannelDao.selectList(wrapper);

        return channels.stream().map(channel -> {
            MerchantChannelDTO dto = convertToDTO(channel);
            // 补充渠道信息
            SaasPaymentChannel paymentChannel = paymentChannelDao.selectById(channel.getChannelId());
            if (paymentChannel != null) {
                dto.setChannelCode(paymentChannel.getChannelCode());
                dto.setChannelName(paymentChannel.getChannelName());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private MerchantChannelDTO convertToDTO(SaasMerchantChannel channel) {
        MerchantChannelDTO dto = new MerchantChannelDTO();
        BeanUtils.copyProperties(channel, dto);
        return dto;
    }
}
