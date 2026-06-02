package com.lzlj.account.paymentchannel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.domain.paymentchannel.PaymentChannelDTO;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.config.SaaSApiClient;
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

import java.util.ArrayList;
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
    private final SaaSApiClient saasApiClient;

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
    public int syncFromSaas() {
        log.info("从 SaaS 同步支付通道开始");

        // 调用 SaaS 获取所有支付通道（不限制状态，获取全部）
        Result<List<PaymentChannelDTO>> result = saasApiClient.listPaymentChannels(null);
        if (!result.isSuccess()) {
            log.error("从 SaaS 获取支付通道列表失败: code={}, message={}", result.getCode(), result.getMessage());
            return 0;
        }

        List<PaymentChannelDTO> saasChannels = result.getData();
        if (saasChannels == null) {
            saasChannels = new ArrayList<>();
        }

        log.info("从 SaaS 获取到 {} 条支付通道", saasChannels.size());

        // 1. 获取 SaaS 返回的所有 channelCode
        List<String> saasChannelCodes = saasChannels.stream()
                .map(PaymentChannelDTO::getChannelCode)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());

        // 2. 查询本地所有未删除的通道
        LambdaQueryWrapper<LzljPaymentChannel> localWrapper = new LambdaQueryWrapper<>();
        localWrapper.eq(LzljPaymentChannel::getDeleted, 0);
        List<LzljPaymentChannel> localChannels = paymentChannelDao.selectList(localWrapper);

        // 3. 找出本地有但 SaaS 没有的通道，执行软删除
        List<String> localChannelCodes = localChannels.stream()
                .map(LzljPaymentChannel::getChannelCode)
                .collect(Collectors.toList());
        List<String> toDeleteCodes = localChannelCodes.stream()
                .filter(code -> !saasChannelCodes.contains(code))
                .collect(Collectors.toList());

        int deletedCount = 0;
        for (String deleteCode : toDeleteCodes) {
            LambdaQueryWrapper<LzljPaymentChannel> delWrapper = new LambdaQueryWrapper<>();
            delWrapper.eq(LzljPaymentChannel::getChannelCode, deleteCode)
                      .eq(LzljPaymentChannel::getDeleted, 0);
            LzljPaymentChannel toDelete = paymentChannelDao.selectOne(delWrapper);
            if (toDelete != null) {
                toDelete.setDeleted(1);
                paymentChannelDao.updateById(toDelete);
                log.info("删除支付通道（软删除）: channelCode={}", deleteCode);
                deletedCount++;
            }
        }

        // 4. UPSERT：存在则更新，不存在则新增
        int upsertCount = 0;
        for (PaymentChannelDTO saasChannel : saasChannels) {
            String channelCode = saasChannel.getChannelCode();
            if (!StringUtils.hasText(channelCode)) {
                continue;
            }

            LambdaQueryWrapper<LzljPaymentChannel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LzljPaymentChannel::getChannelCode, channelCode)
                   .eq(LzljPaymentChannel::getDeleted, 0);
            LzljPaymentChannel existChannel = paymentChannelDao.selectOne(wrapper);

            if (existChannel != null) {
                // 更新
                updateFromSaasData(existChannel, saasChannel);
                paymentChannelDao.updateById(existChannel);
                log.debug("更新支付通道: channelCode={}", channelCode);
            } else {
                // 新增
                LzljPaymentChannel newChannel = new LzljPaymentChannel();
                updateFromSaasData(newChannel, saasChannel);
                paymentChannelDao.insert(newChannel);
                log.debug("新增支付通道: channelCode={}", channelCode);
            }
            upsertCount++;
        }

        log.info("从 SaaS 同步支付通道完成: 新增/更新 {} 条, 删除 {} 条", upsertCount, deletedCount);
        return upsertCount;
    }

    private void updateFromSaasData(LzljPaymentChannel channel, PaymentChannelDTO saasData) {
        channel.setChannelCode(saasData.getChannelCode());
        channel.setChannelName(saasData.getChannelName());
        channel.setPaymentMethod(saasData.getPaymentMethod());
        channel.setStatus(saasData.getStatus());
        channel.setCloudAccountFee(saasData.getCloudAccountFee());
        channel.setUpstreamCostFee(saasData.getUpstreamCostFee());
        channel.setTotalFeeCost(saasData.getTotalFeeCost());
        channel.setPerTransactionLimit(saasData.getPerTransactionLimit());
    }

    private LzljPaymentChannelDTO convertToDTO(LzljPaymentChannel channel) {
        LzljPaymentChannelDTO dto = new LzljPaymentChannelDTO();
        BeanUtils.copyProperties(channel, dto);
        return dto;
    }
}
