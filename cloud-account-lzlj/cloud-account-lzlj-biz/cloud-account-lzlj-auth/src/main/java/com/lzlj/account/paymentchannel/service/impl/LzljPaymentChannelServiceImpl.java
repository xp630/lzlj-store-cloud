package com.lzlj.account.paymentchannel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.paymentchannel.dao.LzljPaymentChannelDao;
import com.lzlj.account.paymentchannel.dto.LzljPaymentChannelDTO;
import com.lzlj.account.paymentchannel.dto.LzljPaymentChannelQueryDTO;
import com.lzlj.account.paymentchannel.entity.LzljPaymentChannel;
import com.lzlj.account.paymentchannel.service.LzljPaymentChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * LZLJ 支付通道服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljPaymentChannelServiceImpl implements LzljPaymentChannelService {

    private final LzljPaymentChannelDao paymentChannelDao;

    @Override
    public LzljPaymentChannelDTO getById(Long id) {
        LzljPaymentChannel channel = paymentChannelDao.selectById(id);
        if (channel == null) {
            return null;
        }
        return convertToDTO(channel);
    }

    @Override
    public PageResult<LzljPaymentChannelDTO> page(LzljPaymentChannelQueryDTO query) {
        LambdaQueryWrapper<LzljPaymentChannel> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getChannelCode())) {
            wrapper.eq(LzljPaymentChannel::getChannelCode, query.getChannelCode());
        }
        if (StringUtils.hasText(query.getChannelName())) {
            wrapper.like(LzljPaymentChannel::getChannelName, query.getChannelName());
        }
        if (StringUtils.hasText(query.getChannelType())) {
            wrapper.eq(LzljPaymentChannel::getChannelType, query.getChannelType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(LzljPaymentChannel::getStatus, query.getStatus());
        }
        wrapper.eq(LzljPaymentChannel::getDeleted, 0)
               .orderByAsc(LzljPaymentChannel::getId);

        IPage<LzljPaymentChannel> page = paymentChannelDao.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()),
                wrapper
        );

        List<LzljPaymentChannelDTO> records = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<LzljPaymentChannelDTO> listEnabled() {
        LambdaQueryWrapper<LzljPaymentChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljPaymentChannel::getStatus, 1)
               .eq(LzljPaymentChannel::getDeleted, 0)
               .orderByAsc(LzljPaymentChannel::getId);

        return paymentChannelDao.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void syncFromExternal() {
        // TODO: 从SaaS同步支付通道数据
        // SaaS是支付通道配置的源头，LZLJ通过此方法同步
        // 实现时需要：
        // 1. 调用SaaS支付通道列表接口
        // 2. 遍历数据，根据channelCode判断是新增还是更新
        // 3. 幂等处理：已存在的根据channelCode更新，不存在的插入
        log.info("同步支付通道数据（待实现，需调用SaaS API）");
    }

    private LzljPaymentChannelDTO convertToDTO(LzljPaymentChannel channel) {
        LzljPaymentChannelDTO dto = new LzljPaymentChannelDTO();
        BeanUtils.copyProperties(channel, dto);
        return dto;
    }
}
