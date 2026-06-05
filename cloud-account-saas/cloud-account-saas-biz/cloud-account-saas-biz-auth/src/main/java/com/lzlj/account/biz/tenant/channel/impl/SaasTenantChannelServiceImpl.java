package com.lzlj.account.biz.tenant.channel.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.biz.paychannel.dao.SaasPaymentChannelDao;
import com.lzlj.account.biz.paychannel.entity.SaasPaymentChannel;
import com.lzlj.account.biz.tenant.channel.CreateTenantChannelDTO;
import com.lzlj.account.biz.tenant.channel.SaasTenantChannel;
import com.lzlj.account.biz.tenant.channel.dao.SaasTenantChannelDao;
import com.lzlj.account.biz.tenant.channel.TenantChannelDTO;
import com.lzlj.account.biz.tenant.channel.SaasTenantChannelService;
import com.lzlj.account.biz.tenant.channel.UpdateTenantChannelDTO;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租户渠道服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaasTenantChannelServiceImpl implements SaasTenantChannelService {

    private final SaasTenantChannelDao tenantChannelDao;
    private final SaasPaymentChannelDao paymentChannelDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createChannels(Long tenantId, List<CreateTenantChannelDTO> channels) {
        if (channels == null || channels.isEmpty()) {
            return;
        }

        for (CreateTenantChannelDTO channel : channels) {
            // 检查是否已存在
            if (exists(tenantId, channel.getChannelId())) {
                log.warn("租户{}已开通渠道{}", tenantId, channel.getChannelId());
                continue;
            }

            // 检查渠道是否存在
            SaasPaymentChannel paymentChannel = paymentChannelDao.selectById(channel.getChannelId());
            if (paymentChannel == null) {
                log.warn("渠道{}不存在，跳过", channel.getChannelId());
                continue;
            }

            SaasTenantChannel tenantChannel = new SaasTenantChannel();
            tenantChannel.setTenantId(tenantId);
            tenantChannel.setChannelId(channel.getChannelId());
            tenantChannel.setStatus(1); // 默认开通
            tenantChannel.setRateType(1); // 固定费率
            tenantChannel.setRateValue(channel.getRateValue() != null ? channel.getRateValue() : BigDecimal.ZERO);

            tenantChannelDao.insert(tenantChannel);
            log.info("为租户{}开通渠道{}成功", tenantId, channel.getChannelId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChannel(Long tenantId, Long channelId, UpdateTenantChannelDTO dto) {
        SaasTenantChannel tenantChannel = getByTenantAndChannel(tenantId, channelId);
        if (tenantChannel == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (dto.getStatus() != null) {
            tenantChannel.setStatus(dto.getStatus());
        }
        if (dto.getRateValue() != null) {
            tenantChannel.setRateValue(dto.getRateValue());
        }

        tenantChannelDao.updateById(tenantChannel);
        log.info("更新租户{}渠道{}成功", tenantId, channelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChannel(Long tenantId, Long channelId) {
        LambdaQueryWrapper<SaasTenantChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantChannel::getTenantId, tenantId)
               .eq(SaasTenantChannel::getChannelId, channelId);
        tenantChannelDao.delete(wrapper);
        log.info("删除租户{}渠道{}成功", tenantId, channelId);
    }

    @Override
    public List<TenantChannelDTO> getByTenantId(Long tenantId) {
        LambdaQueryWrapper<SaasTenantChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantChannel::getTenantId, tenantId);
        List<SaasTenantChannel> channels = tenantChannelDao.selectList(wrapper);

        return channels.stream().map(channel -> {
            TenantChannelDTO dto = convertToDTO(channel);
            // 补充渠道信息
            SaasPaymentChannel paymentChannel = paymentChannelDao.selectById(channel.getChannelId());
            if (paymentChannel != null) {
                dto.setChannelCode(paymentChannel.getChannelCode());
                dto.setChannelName(paymentChannel.getChannelName());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByTenantId(Long tenantId) {
        LambdaQueryWrapper<SaasTenantChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantChannel::getTenantId, tenantId);
        tenantChannelDao.delete(wrapper);
        log.info("删除租户{}所有渠道", tenantId);
    }

    private boolean exists(Long tenantId, Long channelId) {
        LambdaQueryWrapper<SaasTenantChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantChannel::getTenantId, tenantId)
               .eq(SaasTenantChannel::getChannelId, channelId);
        return tenantChannelDao.selectCount(wrapper) > 0;
    }

    private SaasTenantChannel getByTenantAndChannel(Long tenantId, Long channelId) {
        LambdaQueryWrapper<SaasTenantChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantChannel::getTenantId, tenantId)
               .eq(SaasTenantChannel::getChannelId, channelId);
        return tenantChannelDao.selectOne(wrapper);
    }

    private TenantChannelDTO convertToDTO(SaasTenantChannel channel) {
        TenantChannelDTO dto = new TenantChannelDTO();
        BeanUtils.copyProperties(channel, dto);
        return dto;
    }
}
