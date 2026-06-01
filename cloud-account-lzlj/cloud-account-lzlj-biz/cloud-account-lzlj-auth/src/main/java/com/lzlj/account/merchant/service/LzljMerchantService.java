package com.lzlj.account.merchant.service;

import com.lzlj.account.merchant.dto.*;
import com.lzlj.account.common.core.domain.PageResult;

import java.util.List;

/**
 * LZLJ 商户服务接口
 */
public interface LzljMerchantService {

    /**
     * 同步商户（支持多种数据来源）
     * 如果 dto 中有 merchantCode，自动从 SaaS 获取数据
     */
    MerchantDTO syncMerchant(SyncMerchantDTO dto);

    /**
     * 创建商户
     */
    MerchantDTO create(CreateMerchantDTO dto);

    /**
     * 更新商户
     */
    void update(Long id, UpdateMerchantDTO dto);

    /**
     * 删除商户
     */
    void delete(Long id);

    /**
     * 获取商户详情
     */
    MerchantDTO getById(Long id);

    /**
     * 商户分页列表
     */
    PageResult<MerchantDTO> page(MerchantQueryDTO query);

    /**
     * 获取结算信息
     */
    SettlementInfoDTO getSettlement(Long merchantId);

    /**
     * 更新结算信息
     */
    void updateSettlement(Long merchantId, SettlementInfoDTO dto);

    /**
     * 获取商户账号列表
     */
    List<MerchantUserDTO> getUsers(Long merchantId);

    /**
     * 关联用户到商户
     */
    void assignUser(Long merchantId, AssignMerchantUserDTO dto);

    /**
     * 解绑商户用户
     */
    void unbindUser(Long merchantId, Long userId);

    /**
     * 根据机构ID获取业务场景
     */
    List<Long> getScenarioIdsByOrgId(Long orgId);

    /**
     * 从 SaaS 批量同步所有母户
     * @param keyword 关键字（可选，模糊搜索）
     * @return 同步的商户数量
     */
    int syncAllFromSaas(String keyword);
}
