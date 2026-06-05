package com.lzlj.account.biz.tenant.channel;

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
     * 批量删除租户渠道
     */
    void deleteByTenantId(Long tenantId);
}
