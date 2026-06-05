package com.lzlj.account.biz.tenantchannel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.biz.paychannel.dao.SaasPaymentChannelDao;
import com.lzlj.account.biz.paychannel.entity.SaasPaymentChannel;
import com.lzlj.account.biz.tenant.channel.TenantChannelDetailDTO;
import com.lzlj.account.biz.tenantchannel.TenantChannelDTO;
import com.lzlj.account.biz.tenantchannel.UpdateTenantChannelDTO;
import com.lzlj.account.biz.tenantchannel.dao.SaasTenantChannelDao;
import com.lzlj.account.biz.tenantchannel.dto.CreateTenantChannelDTO;
import com.lzlj.account.biz.tenantchannel.dto.TenantChannelQueryDTO;
import com.lzlj.account.biz.tenant.dao.TenantDao;
import com.lzlj.account.biz.tenant.entity.Tenant;
import com.lzlj.account.biz.tenantchannel.entity.SaasTenantChannel;
import com.lzlj.account.biz.tenantchannel.service.SaasTenantChannelService;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
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
    private final TenantDao tenantDao;

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
    public List<TenantChannelDetailDTO> getDetailByTenantId(Long tenantId) {
        LambdaQueryWrapper<SaasTenantChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasTenantChannel::getTenantId, tenantId);
        List<SaasTenantChannel> channels = tenantChannelDao.selectList(wrapper);

        return channels.stream().map(channel -> {
            TenantChannelDetailDTO dto = new TenantChannelDetailDTO();
            // 租户渠道信息
            dto.setId(channel.getId());
            dto.setTenantId(channel.getTenantId());
            dto.setChannelId(channel.getChannelId());
            dto.setStatus(channel.getStatus());
            dto.setRateType(channel.getRateType());
            dto.setRateValue(channel.getRateValue());
            dto.setCreateTime(channel.getCreateTime());

            // 标准渠道信息
            SaasPaymentChannel paymentChannel = paymentChannelDao.selectById(channel.getChannelId());
            if (paymentChannel != null) {
                dto.setChannelCode(paymentChannel.getChannelCode());
                dto.setChannelName(paymentChannel.getChannelName());
                dto.setPaymentMethod(paymentChannel.getPaymentMethod());
                dto.setCloudAccountFee(paymentChannel.getCloudAccountFee());
                dto.setUpstreamCostFee(paymentChannel.getUpstreamCostFee());
                dto.setTotalFeeCost(paymentChannel.getTotalFeeCost());
                dto.setPerTransactionLimit(paymentChannel.getPerTransactionLimit());
            }

            // 租户名称
            Tenant tenant = tenantDao.selectById(channel.getTenantId());
            if (tenant != null) {
                dto.setTenantName(tenant.getTenantName());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public PageResult<TenantChannelDetailDTO> page(TenantChannelQueryDTO queryDTO, Integer pageNum, Integer pageSize) {
        // 构建分页对象
        Page<SaasTenantChannel> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        LambdaQueryWrapper<SaasTenantChannel> wrapper = new LambdaQueryWrapper<>();

        // 按租户ID精确查询
        if (queryDTO.getTenantId() != null) {
            wrapper.eq(SaasTenantChannel::getTenantId, queryDTO.getTenantId());
        }

        // 按渠道ID精确查询
        if (queryDTO.getChannelId() != null) {
            wrapper.eq(SaasTenantChannel::getChannelId, queryDTO.getChannelId());
        }

        // 按渠道编码精确查询
        if (StringUtils.hasText(queryDTO.getChannelCode())) {
            wrapper.eq(SaasTenantChannel::getChannelId, queryDTO.getChannelId());
        }

        // 按状态精确查询
        if (queryDTO.getStatus() != null) {
            wrapper.eq(SaasTenantChannel::getStatus, queryDTO.getStatus());
        }

        // 按租户名称模糊查询（需要先获取匹配的租户IDs）
        if (StringUtils.hasText(queryDTO.getTenantName())) {
            LambdaQueryWrapper<Tenant> tenantWrapper = new LambdaQueryWrapper<>();
            tenantWrapper.like(Tenant::getTenantName, queryDTO.getTenantName());
            List<Tenant> tenants = tenantDao.selectList(tenantWrapper);
            if (tenants.isEmpty()) {
                // 没有匹配租户，返回空结果
                return new PageResult<>(Collections.emptyList(), page.getTotal(), page.getCurrent(), page.getSize());
            }
            List<Long> tenantIds = tenants.stream().map(Tenant::getId).collect(Collectors.toList());
            wrapper.in(SaasTenantChannel::getTenantId, tenantIds);
        }

        // 查询分页数据
        IPage<SaasTenantChannel> resultPage = tenantChannelDao.selectPage(page, wrapper);

        // 补充渠道信息和租户名称
        List<TenantChannelDetailDTO> records = resultPage.getRecords().stream().map(channel -> {
            TenantChannelDetailDTO dto = new TenantChannelDetailDTO();
            dto.setId(channel.getId());
            dto.setTenantId(channel.getTenantId());
            dto.setChannelId(channel.getChannelId());
            dto.setStatus(channel.getStatus());
            dto.setRateType(channel.getRateType());
            dto.setRateValue(channel.getRateValue());
            dto.setCreateTime(channel.getCreateTime());

            // 标准渠道信息
            SaasPaymentChannel paymentChannel = paymentChannelDao.selectById(channel.getChannelId());
            if (paymentChannel != null) {
                dto.setChannelCode(paymentChannel.getChannelCode());
                dto.setChannelName(paymentChannel.getChannelName());
                dto.setPaymentMethod(paymentChannel.getPaymentMethod());
                dto.setCloudAccountFee(paymentChannel.getCloudAccountFee());
                dto.setUpstreamCostFee(paymentChannel.getUpstreamCostFee());
                dto.setTotalFeeCost(paymentChannel.getTotalFeeCost());
                dto.setPerTransactionLimit(paymentChannel.getPerTransactionLimit());
            }

            // 租户名称
            Tenant tenant = tenantDao.selectById(channel.getTenantId());
            if (tenant != null) {
                dto.setTenantName(tenant.getTenantName());
            }
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(records, resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize());
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
