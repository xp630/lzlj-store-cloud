package com.lzlj.account.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.merchant.dao.MerchantDao;
import com.lzlj.account.merchant.dao.MerchantLegalDao;
import com.lzlj.account.merchant.dao.MerchantChannelAccountDao;
import com.lzlj.account.merchant.dto.CreateMerchantDTO;
import com.lzlj.account.merchant.dto.MerchantDTO;
import com.lzlj.account.merchant.dto.UpdateMerchantDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantChannelAccountDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantLegalDTO;
import com.lzlj.account.merchant.entity.Merchant;
import com.lzlj.account.merchant.entity.MerchantLegal;
import com.lzlj.account.merchant.entity.MerchantChannelAccount;
import com.lzlj.account.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantDao merchantDao;
    private final MerchantLegalDao merchantLegalDao;
    private final MerchantChannelAccountDao merchantChannelAccountDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreateMerchantDTO dto) {
        // 检查编码唯一性
        if (checkCodeExists(dto.getMerchantCode(), null)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "商户编码已存在");
        }

        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(dto, merchant);
        merchant.setStatus(1); // 默认启用

        merchantDao.insert(merchant);

        // 保存法人信息
        saveMerchantLegal(merchant.getId(), dto.getLegal());

        // 保存银联账户
        saveMerchantChannelAccounts(merchant.getId(), dto.getChannelAccounts());

        log.info("创建商户成功: id={}, code={}", merchant.getId(), merchant.getMerchantCode());
        return merchant.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, UpdateMerchantDTO dto) {
        Merchant merchant = merchantDao.selectById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (StringUtils.hasText(dto.getMerchantName())) {
            merchant.setMerchantName(dto.getMerchantName());
        }
        if (dto.getShortName() != null) {
            merchant.setShortName(dto.getShortName());
        }
        if (dto.getContact() != null) {
            merchant.setContact(dto.getContact());
        }
        if (dto.getContactPhone() != null) {
            merchant.setContactPhone(dto.getContactPhone());
        }
        if (dto.getContactEmail() != null) {
            merchant.setContactEmail(dto.getContactEmail());
        }
        if (dto.getAddress() != null) {
            merchant.setAddress(dto.getAddress());
        }
        if (dto.getWangshangAccount() != null) {
            merchant.setWangshangAccount(dto.getWangshangAccount());
        }
        if (dto.getStatus() != null) {
            merchant.setStatus(dto.getStatus());
        }

        merchantDao.updateById(merchant);

        // 更新法人信息
        saveMerchantLegal(id, dto.getLegal());

        // 更新银联账户
        saveMerchantChannelAccounts(id, dto.getChannelAccounts());

        log.info("更新商户成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Merchant merchant = merchantDao.selectById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 删除法人信息
        LambdaQueryWrapper<MerchantLegal> legalWrapper = new LambdaQueryWrapper<>();
        legalWrapper.eq(MerchantLegal::getMerchantId, id);
        merchantLegalDao.delete(legalWrapper);

        // 删除银联账户
        LambdaQueryWrapper<MerchantChannelAccount> channelWrapper = new LambdaQueryWrapper<>();
        channelWrapper.eq(MerchantChannelAccount::getMerchantId, id);
        merchantChannelAccountDao.delete(channelWrapper);

        // 删除商户
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

        // 从独立表查询法人信息
        LambdaQueryWrapper<MerchantLegal> legalWrapper = new LambdaQueryWrapper<>();
        legalWrapper.eq(MerchantLegal::getMerchantId, merchant.getId())
                   .eq(MerchantLegal::getDeleted, 0);
        MerchantLegal legal = merchantLegalDao.selectOne(legalWrapper);
        if (legal != null) {
            MerchantLegalDTO legalDTO = new MerchantLegalDTO();
            legalDTO.setLicenseNo(legal.getLicenseNo());
            legalDTO.setLegalPerson(legal.getLegalPerson());
            legalDTO.setLicensePic(legal.getLicensePic());
            legalDTO.setLegalIdCard(legal.getLegalIdCard());
            legalDTO.setLegalIdCardPic(legal.getLegalIdCardPic());
            dto.setLegal(legalDTO);
        }

        // 从独立表查询银联账户
        LambdaQueryWrapper<MerchantChannelAccount> channelWrapper = new LambdaQueryWrapper<>();
        channelWrapper.eq(MerchantChannelAccount::getMerchantId, merchant.getId())
                     .eq(MerchantChannelAccount::getDeleted, 0);
        List<MerchantChannelAccount> channelAccounts = merchantChannelAccountDao.selectList(channelWrapper);
        if (channelAccounts != null && !channelAccounts.isEmpty()) {
            List<MerchantChannelAccountDTO> channelDTOList = channelAccounts.stream().map(ca -> {
                MerchantChannelAccountDTO caDTO = new MerchantChannelAccountDTO();
                caDTO.setChannelId(ca.getChannelId());
                caDTO.setUnionPayAccount(ca.getUnionPayAccount());
                caDTO.setAccountName(ca.getAccountName());
                caDTO.setStatus(ca.getStatus());
                return caDTO;
            }).collect(Collectors.toList());
            dto.setChannelAccounts(channelDTOList);
        }

        return dto;
    }

    private void saveMerchantLegal(Long merchantId, MerchantLegalDTO legalDTO) {
        if (legalDTO == null) {
            return;
        }
        // 查询是否已存在法人信息
        LambdaQueryWrapper<MerchantLegal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantLegal::getMerchantId, merchantId)
               .eq(MerchantLegal::getDeleted, 0);
        MerchantLegal existingLegal = merchantLegalDao.selectOne(wrapper);

        if (existingLegal != null) {
            // 更新现有法人信息
            if (StringUtils.hasText(legalDTO.getLicenseNo())) {
                existingLegal.setLicenseNo(legalDTO.getLicenseNo());
            }
            if (StringUtils.hasText(legalDTO.getLegalPerson())) {
                existingLegal.setLegalPerson(legalDTO.getLegalPerson());
            }
            if (StringUtils.hasText(legalDTO.getLicensePic())) {
                existingLegal.setLicensePic(legalDTO.getLicensePic());
            }
            if (StringUtils.hasText(legalDTO.getLegalIdCard())) {
                existingLegal.setLegalIdCard(legalDTO.getLegalIdCard());
            }
            if (StringUtils.hasText(legalDTO.getLegalIdCardPic())) {
                existingLegal.setLegalIdCardPic(legalDTO.getLegalIdCardPic());
            }
            merchantLegalDao.updateById(existingLegal);
        } else {
            // 新增法人信息
            MerchantLegal newLegal = new MerchantLegal();
            newLegal.setMerchantId(merchantId);
            newLegal.setLicenseNo(legalDTO.getLicenseNo());
            newLegal.setLegalPerson(legalDTO.getLegalPerson());
            newLegal.setLicensePic(legalDTO.getLicensePic());
            newLegal.setLegalIdCard(legalDTO.getLegalIdCard());
            newLegal.setLegalIdCardPic(legalDTO.getLegalIdCardPic());
            newLegal.setStatus(1);
            merchantLegalDao.insert(newLegal);
        }
    }

    private void saveMerchantChannelAccounts(Long merchantId, List<MerchantChannelAccountDTO> channelAccounts) {
        if (channelAccounts == null || channelAccounts.isEmpty()) {
            return;
        }
        // 先删除现有的银联账户
        LambdaQueryWrapper<MerchantChannelAccount> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(MerchantChannelAccount::getMerchantId, merchantId);
        merchantChannelAccountDao.delete(delWrapper);

        // 新增银联账户
        for (MerchantChannelAccountDTO caDTO : channelAccounts) {
            MerchantChannelAccount ca = new MerchantChannelAccount();
            ca.setMerchantId(merchantId);
            ca.setChannelId(caDTO.getChannelId());
            ca.setUnionPayAccount(caDTO.getUnionPayAccount());
            ca.setAccountName(caDTO.getAccountName());
            ca.setStatus(caDTO.getStatus() != null ? caDTO.getStatus() : 1);
            merchantChannelAccountDao.insert(ca);
        }
    }
}
