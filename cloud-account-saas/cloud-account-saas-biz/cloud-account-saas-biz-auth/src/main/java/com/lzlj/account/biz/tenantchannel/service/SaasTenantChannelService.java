package com.lzlj.account.biz.tenantchannel.service;

import com.lzlj.account.biz.tenant.channel.TenantChannelDetailDTO;
import com.lzlj.account.biz.tenantchannel.TenantChannelDTO;
import com.lzlj.account.biz.tenantchannel.UpdateTenantChannelDTO;
import com.lzlj.account.biz.tenantchannel.dto.CreateTenantChannelDTO;
import com.lzlj.account.biz.tenantchannel.dto.TenantChannelQueryDTO;
import com.lzlj.account.common.core.domain.PageResult;

import java.util.List;

/**
 * 租户渠道服务接口
 */
public interface SaasTenantChannelService {

    /**
     * 为租户开通渠道
     */
    void createChannels(Long tenantId, List<CreateTenantChannelDTO> channels);

    /**
     * 更新租户渠道
     */
    void updateChannel(Long tenantId, Long channelId, UpdateTenantChannelDTO dto);

    /**
     * 删除租户渠道
     */
    void deleteChannel(Long tenantId, Long channelId);

    /**
     * 获取租户所有渠道
     */
    List<TenantChannelDTO> getByTenantId(Long tenantId);

    /**
     * 获取租户渠道详情（包含标准渠道信息和租户费率）
     */
    List<TenantChannelDetailDTO> getDetailByTenantId(Long tenantId);

    /**
     * 分页查询租户渠道详情
     * @param queryDTO 查询条件
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    PageResult<TenantChannelDetailDTO> page(TenantChannelQueryDTO queryDTO, Integer pageNum, Integer pageSize);

    /**
     * 批量删除租户渠道
     */
    void deleteByTenantId(Long tenantId);
}
