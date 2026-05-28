package com.lzlj.account.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.merchant.dao.MerchantDao;
import com.lzlj.account.merchant.dto.CreateMerchantDTO;
import com.lzlj.account.merchant.dto.MerchantDTO;
import com.lzlj.account.merchant.dto.UpdateMerchantDTO;
import com.lzlj.account.merchant.entity.Merchant;
import com.lzlj.merchant.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

/**
 * 商户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantDao merchantDao;

    @Override
    public Long create(CreateMerchantDTO dto) {
        // 检查编码唯一性
        if (checkCodeExists(dto.getMerchantCode(), null)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "商户编码已存在");
        }

        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(dto, merchant);
        merchant.setStatus(1); // 默认启用

        merchantDao.insert(merchant);
        log.info("创建商户成功: id={}, code={}", merchant.getId(), merchant.getMerchantCode());

        return merchant.getId();
    }

    @Override
    public void update(Long id, UpdateMerchantDTO dto) {
        Merchant existMerchant = merchantDao.selectById(id);
        if (existMerchant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, existMerchant);
        merchantDao.updateById(existMerchant);

        log.info("更新商户成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        Merchant merchant = merchantDao.selectById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        merchantDao.deleteById(id);
        log.info("删除商户成功: id={}", id);
    }

    @Override
    public MerchantDTO getById(Long id) {
        Merchant merchant = merchantDao.selectById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(merchant);
    }

    @Override
    public MerchantDTO getByCode(String merchantCode) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getMerchantCode, merchantCode)
               .eq(Merchant::getDeleted, 0);
        Merchant merchant = merchantDao.selectOne(wrapper);
        if (merchant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "商户不存在");
        }
        return convertToDTO(merchant);
    }

    @Override
    public PageResult<MerchantDTO> page(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Merchant::getMerchantName, keyword)
               .eq(status != null, Merchant::getStatus, status)
               .eq(Merchant::getDeleted, 0)
               .orderByDesc(Merchant::getCreateTime);

        IPage<Merchant> resultPage = merchantDao.selectPage(page, wrapper);

        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        Merchant merchant = merchantDao.selectById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        merchant.setStatus(status);
        merchantDao.updateById(merchant);
        log.info("修改商户状态: id={}, status={}", id, status);
    }

    private boolean checkCodeExists(String merchantCode, Long excludeId) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (merchantCode != null) {
            wrapper.eq(Merchant::getMerchantCode, merchantCode);
        }
        if (excludeId != null) {
            wrapper.ne(Merchant::getId, excludeId);
        }
        wrapper.eq(Merchant::getDeleted, 0);
        return merchantDao.selectCount(wrapper) > 0;
    }

    private MerchantDTO convertToDTO(Merchant merchant) {
        MerchantDTO dto = new MerchantDTO();
        BeanUtils.copyProperties(merchant, dto);
        return dto;
    }
}
