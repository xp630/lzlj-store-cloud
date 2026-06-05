package com.lzlj.account.biz.merchant.service;

import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.biz.merchant.dto.CreateMerchantDTO;
import com.lzlj.account.biz.merchant.dto.MerchantDTO;
import com.lzlj.account.biz.merchant.dto.MerchantQueryDTO;
import com.lzlj.account.biz.merchant.dto.UpdateMerchantDTO;

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
    PageResult<MerchantDTO> page(PageRequest<MerchantQueryDTO> pageRequest);

    /**
     * 修改商户状态
     */
    void changeStatus(Long id, Integer status);
}
