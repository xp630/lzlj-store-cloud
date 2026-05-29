package com.lzlj.account.paymentchannel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.paymentchannel.dao.PaymentChannelDao;
import com.lzlj.account.paymentchannel.dto.CreatePaymentChannelDTO;
import com.lzlj.account.paymentchannel.dto.PaymentChannelDTO;
import com.lzlj.account.paymentchannel.dto.PaymentChannelQueryDTO;
import com.lzlj.account.paymentchannel.dto.UpdatePaymentChannelDTO;
import com.lzlj.account.paymentchannel.entity.PaymentChannel;
import com.lzlj.account.paymentchannel.service.PaymentChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付通道服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentChannelServiceImpl implements PaymentChannelService {

    private final PaymentChannelDao paymentChannelDao;

    @Override
    public Long create(CreatePaymentChannelDTO dto) {
        // 检查通道编码唯一性
        if (checkCodeExists(dto.getChannelCode(), null)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "通道编码已存在");
        }

        PaymentChannel channel = new PaymentChannel();
        BeanUtils.copyProperties(dto, channel);
        paymentChannelDao.insert(channel);
        log.info("创建支付通道成功: id={}, channelCode={}", channel.getId(), channel.getChannelCode());
        return channel.getId();
    }

    @Override
    public void update(Long id, UpdatePaymentChannelDTO dto) {
        PaymentChannel existChannel = paymentChannelDao.selectById(id);
        if (existChannel == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, existChannel);
        paymentChannelDao.updateById(existChannel);
        log.info("更新支付通道成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        PaymentChannel channel = paymentChannelDao.selectById(id);
        if (channel == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        paymentChannelDao.deleteById(id);
        log.info("删除支付通道成功: id={}", id);
    }

    @Override
    public PaymentChannelDTO getById(Long id) {
        PaymentChannel channel = paymentChannelDao.selectById(id);
        if (channel == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(channel);
    }

    @Override
    public PageResult<PaymentChannelDTO> page(PaymentChannelQueryDTO query, Integer pageNum, Integer pageSize) {
        Page<PaymentChannel> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PaymentChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getChannelName()), PaymentChannel::getChannelCode, query.getChannelName())
               .eq(query.getStatus() != null, PaymentChannel::getStatus, query.getStatus())
               .orderByDesc(PaymentChannel::getCreateTime);

        IPage<PaymentChannel> resultPage = paymentChannelDao.selectPage(page, wrapper);

        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public List<PaymentChannelDTO> list() {
        LambdaQueryWrapper<PaymentChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PaymentChannel::getCreateTime);
        List<PaymentChannel> channels = paymentChannelDao.selectList(wrapper);
        return channels.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private boolean checkCodeExists(String channelCode, Long excludeId) {
        LambdaQueryWrapper<PaymentChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentChannel::getChannelCode, channelCode);
        if (excludeId != null) {
            wrapper.ne(PaymentChannel::getId, excludeId);
        }
        return paymentChannelDao.selectCount(wrapper) > 0;
    }

    private PaymentChannelDTO convertToDTO(PaymentChannel channel) {
        PaymentChannelDTO dto = new PaymentChannelDTO();
        BeanUtils.copyProperties(channel, dto);
        return dto;
    }
}
