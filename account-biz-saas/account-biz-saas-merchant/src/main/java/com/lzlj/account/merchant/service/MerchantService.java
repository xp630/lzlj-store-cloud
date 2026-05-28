package com.lzlj.merchant.merchant.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.merchant.merchant.dto.CreateMerchantDTO;
import com.lzlj.merchant.merchant.dto.MerchantDTO;
import com.lzlj.merchant.merchant.dto.UpdateMerchantDTO;

/**
 * 商户服务接口
 */
public interface MerchantService {

    /**
     * 创建商户
     */
    Long create(CreateMerchantDTO dto);

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
     * 根据编码获取商户
     */
    MerchantDTO getByCode(String merchantCode);

    /**
     * 分页查询商户
     */
    PageResult<MerchantDTO> page(String keyword, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 修改商户状态
     */
    void changeStatus(Long id, Integer status);
}
