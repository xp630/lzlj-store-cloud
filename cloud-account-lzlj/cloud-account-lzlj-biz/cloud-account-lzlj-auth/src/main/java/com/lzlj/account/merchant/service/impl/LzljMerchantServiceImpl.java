package com.lzlj.account.merchant.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.merchant.dao.LzljMerchantDao;
import com.lzlj.account.merchant.dao.LzljMerchantUserDao;
import com.lzlj.account.merchant.dao.LzljSettlementInfoDao;
import com.lzlj.account.merchant.dto.*;
import com.lzlj.account.merchant.entity.LzljMerchant;
import com.lzlj.account.merchant.entity.LzljMerchantUser;
import com.lzlj.account.merchant.entity.LzljSettlementInfo;
import com.lzlj.account.merchant.service.LzljMerchantService;
import com.lzlj.account.user.dao.LzljOrgDao;
import com.lzlj.account.user.dao.LzljUserDao;
import com.lzlj.account.user.entity.LzljOrg;
import com.lzlj.account.user.entity.LzljUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LZLJ 商户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljMerchantServiceImpl implements LzljMerchantService {

    private final LzljMerchantDao merchantDao;
    private final LzljSettlementInfoDao settlementDao;
    private final LzljMerchantUserDao merchantUserDao;
    private final LzljOrgDao orgDao;
    private final LzljUserDao userDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantDTO syncFromWangshang(SyncMerchantDTO dto) {
        // 检查是否已存在（根据 merchant_code）
        LambdaQueryWrapper<LzljMerchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljMerchant::getMerchantCode, dto.getMerchantCode())
               .eq(LzljMerchant::getDeleted, 0);
        LzljMerchant existMerchant = merchantDao.selectOne(wrapper);

        if (existMerchant != null) {
            // 更新
            BeanUtils.copyProperties(dto, existMerchant);
            existMerchant.setStatus(1);
            merchantDao.updateById(existMerchant);

            // 更新母户的业务场景
            updateOrgScenarios(existMerchant.getId(), dto.getScenarios());

            return getById(existMerchant.getId());
        }

        // 创建商户
        LzljMerchant merchant = new LzljMerchant();
        BeanUtils.copyProperties(dto, merchant);
        merchant.setMerchantCode(generateMerchantCode());
        merchant.setStatus(1);
        // 默认设置为母户类型
        if (merchant.getMerchantType() == null) {
            merchant.setMerchantType(1);
        }
        merchantDao.insert(merchant);

        // 创建结算信息（空）
        LzljSettlementInfo settlement = new LzljSettlementInfo();
        settlement.setMerchantId(merchant.getId());
        settlement.setStatus(1);
        settlementDao.insert(settlement);

        // 创建母户机构
        LzljOrg org = createMerchantOrg(merchant, dto.getScenarios());

        MerchantDTO result = convertToMerchantDTO(merchant);
        result.setOrgId(org.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantDTO create(CreateMerchantDTO dto) {
        LzljMerchant merchant = new LzljMerchant();
        BeanUtils.copyProperties(dto, merchant);
        merchant.setMerchantCode(generateMerchantCode());
        merchant.setStatus(1);
        // 默认设置为母户类型
        if (merchant.getMerchantType() == null) {
            merchant.setMerchantType(1);
        }
        merchantDao.insert(merchant);

        // 创建结算信息（空）
        LzljSettlementInfo settlement = new LzljSettlementInfo();
        settlement.setMerchantId(merchant.getId());
        settlement.setStatus(1);
        settlementDao.insert(settlement);

        // 创建母户机构
        LzljOrg org = createMerchantOrg(merchant, dto.getScenarioIds());

        MerchantDTO result = convertToMerchantDTO(merchant);
        result.setOrgId(org.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, UpdateMerchantDTO dto) {
        LzljMerchant merchant = merchantDao.selectById(id);
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
        if (dto.getProvinceCode() != null) {
            merchant.setProvinceCode(dto.getProvinceCode());
        }
        if (dto.getCityCode() != null) {
            merchant.setCityCode(dto.getCityCode());
        }
        if (dto.getDistrictCode() != null) {
            merchant.setDistrictCode(dto.getDistrictCode());
        }
        if (dto.getAddress() != null) {
            merchant.setAddress(dto.getAddress());
        }
        if (dto.getLicenseNo() != null) {
            merchant.setLicenseNo(dto.getLicenseNo());
        }
        if (dto.getLegalPerson() != null) {
            merchant.setLegalPerson(dto.getLegalPerson());
        }
        if (dto.getStatus() != null) {
            merchant.setStatus(dto.getStatus());
        }

        merchantDao.updateById(merchant);

        // 更新业务场景
        if (dto.getScenarioIds() != null) {
            updateOrgScenarios(id, dto.getScenarioIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        LzljMerchant merchant = merchantDao.selectById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 软删除商户
        merchantDao.deleteById(id);

        // 软删除结算信息
        LambdaQueryWrapper<LzljSettlementInfo> settlementWrapper = new LambdaQueryWrapper<>();
        settlementWrapper.eq(LzljSettlementInfo::getMerchantId, id);
        settlementDao.delete(settlementWrapper);

        // 软删除商户用户关联
        LambdaQueryWrapper<LzljMerchantUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(LzljMerchantUser::getMerchantId, id);
        merchantUserDao.delete(userWrapper);
    }

    @Override
    public MerchantDTO getById(Long id) {
        LzljMerchant merchant = merchantDao.selectById(id);
        if (merchant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        MerchantDTO dto = convertToMerchantDTO(merchant);

        // 查询母户机构
        LambdaQueryWrapper<LzljOrg> orgWrapper = new LambdaQueryWrapper<>();
        orgWrapper.eq(LzljOrg::getMerchantId, id)
                  .eq(LzljOrg::getOrgType, 1) // 母户
                  .eq(LzljOrg::getDeleted, 0);
        LzljOrg org = orgDao.selectOne(orgWrapper);
        if (org != null) {
            dto.setOrgId(org.getId());
            dto.setScenarioIds(parseScenarioIds(org.getScenarioIds()));
        }

        return dto;
    }

    @Override
    public PageResult<MerchantDTO> page(MerchantQueryDTO query) {
        LambdaQueryWrapper<LzljMerchant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getMerchantName())) {
            wrapper.like(LzljMerchant::getMerchantName, query.getMerchantName());
        }
        if (StringUtils.hasText(query.getMerchantCode())) {
            wrapper.eq(LzljMerchant::getMerchantCode, query.getMerchantCode());
        }
        if (StringUtils.hasText(query.getContact())) {
            wrapper.like(LzljMerchant::getContact, query.getContact());
        }
        if (query.getStatus() != null) {
            wrapper.eq(LzljMerchant::getStatus, query.getStatus());
        }
        wrapper.eq(LzljMerchant::getDeleted, 0)
               .orderByDesc(LzljMerchant::getCreateTime);

        IPage<LzljMerchant> page = merchantDao.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()),
                wrapper
        );

        List<MerchantDTO> records = page.getRecords().stream()
                .map(this::convertToMerchantDTO)
                .collect(Collectors.toList());

        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public SettlementInfoDTO getSettlement(Long merchantId) {
        LambdaQueryWrapper<LzljSettlementInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljSettlementInfo::getMerchantId, merchantId)
               .eq(LzljSettlementInfo::getDeleted, 0);
        LzljSettlementInfo settlement = settlementDao.selectOne(wrapper);
        if (settlement == null) {
            return null;
        }
        return convertToSettlementDTO(settlement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSettlement(Long merchantId, SettlementInfoDTO dto) {
        LambdaQueryWrapper<LzljSettlementInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljSettlementInfo::getMerchantId, merchantId)
               .eq(LzljSettlementInfo::getDeleted, 0);
        LzljSettlementInfo settlement = settlementDao.selectOne(wrapper);
        if (settlement == null) {
            settlement = new LzljSettlementInfo();
            settlement.setMerchantId(merchantId);
            BeanUtils.copyProperties(dto, settlement);
            settlementDao.insert(settlement);
        } else {
            BeanUtils.copyProperties(dto, settlement);
            settlementDao.updateById(settlement);
        }
    }

    @Override
    public List<MerchantUserDTO> getUsers(Long merchantId) {
        LambdaQueryWrapper<LzljMerchantUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljMerchantUser::getMerchantId, merchantId)
               .eq(LzljMerchantUser::getDeleted, 0);
        List<LzljMerchantUser> merchantUsers = merchantUserDao.selectList(wrapper);

        return merchantUsers.stream().map(mu -> {
            MerchantUserDTO dto = new MerchantUserDTO();
            dto.setId(mu.getId());
            dto.setUserId(mu.getUserId());
            dto.setRole(mu.getRole());
            dto.setStatus(mu.getStatus());

            LzljUser user = userDao.selectById(mu.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
                dto.setRealName(user.getRealName());
                dto.setPhone(user.getPhone());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUser(Long merchantId, AssignMerchantUserDTO dto) {
        // 检查用户是否存在
        LzljUser user = userDao.selectById(dto.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查是否已关联
        LambdaQueryWrapper<LzljMerchantUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljMerchantUser::getMerchantId, merchantId)
               .eq(LzljMerchantUser::getUserId, dto.getUserId())
               .eq(LzljMerchantUser::getDeleted, 0);
        if (merchantUserDao.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
        }

        LzljMerchantUser merchantUser = new LzljMerchantUser();
        merchantUser.setMerchantId(merchantId);
        merchantUser.setUserId(dto.getUserId());
        merchantUser.setRole(dto.getRole());
        merchantUser.setStatus(1);
        merchantUserDao.insert(merchantUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindUser(Long merchantId, Long userId) {
        LambdaQueryWrapper<LzljMerchantUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljMerchantUser::getMerchantId, merchantId)
               .eq(LzljMerchantUser::getUserId, userId)
               .eq(LzljMerchantUser::getDeleted, 0);
        merchantUserDao.delete(wrapper);
    }

    @Override
    public List<Long> getScenarioIdsByOrgId(Long orgId) {
        LzljOrg org = orgDao.selectById(orgId);
        if (org == null) {
            return new ArrayList<>();
        }

        // 如果是子机构，获取顶层机构的业务场景
        Long topOrgId = getTopOrgId(org);
        LzljOrg topOrg = orgDao.selectById(topOrgId);
        if (topOrg == null) {
            return new ArrayList<>();
        }

        return parseScenarioIds(topOrg.getScenarioIds());
    }

    // ==================== 私有方法 ====================

    /**
     * 创建商户对应的机构
     */
    private LzljOrg createMerchantOrg(LzljMerchant merchant, List<Long> scenarioIds) {
        LzljOrg org = new LzljOrg();
        org.setOrgCode("ORG-" + merchant.getMerchantCode());
        org.setOrgName(merchant.getMerchantName());
        // 机构类型跟随商户类型：1=母户商户→母户机构, 2=子户商户→子户机构
        org.setOrgType(merchant.getMerchantType() != null ? merchant.getMerchantType() : 1);
        org.setParentId(0L);
        org.setLevel(1);
        org.setLevelPath("/");
        org.setMerchantId(merchant.getId());
        org.setScenarioIds(JSON.toJSONString(scenarioIds));
        org.setProvinceCode(merchant.getProvinceCode());
        org.setCityCode(merchant.getCityCode());
        org.setDistrictCode(merchant.getDistrictCode());
        org.setAddress(merchant.getAddress());
        org.setContact(merchant.getContact());
        org.setContactPhone(merchant.getContactPhone());
        org.setStatus(1);

        orgDao.insert(org);

        // 回填 level_path
        org.setLevelPath("/" + org.getId() + "/");
        orgDao.updateById(org);

        return org;
    }

    /**
     * 更新母户机构的业务场景
     */
    private void updateOrgScenarios(Long merchantId, List<Long> scenarioIds) {
        LambdaQueryWrapper<LzljOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljOrg::getMerchantId, merchantId)
               .eq(LzljOrg::getOrgType, 1)
               .eq(LzljOrg::getDeleted, 0);
        LzljOrg org = orgDao.selectOne(wrapper);
        if (org != null) {
            org.setScenarioIds(JSON.toJSONString(scenarioIds));
            orgDao.updateById(org);
        }
    }

    /**
     * 生成商户编号
     */
    private String generateMerchantCode() {
        // TODO: 实际实现应该用序列号或分布式ID
        return "M-" + System.currentTimeMillis();
    }

    /**
     * 获取顶层机构ID
     */
    private Long getTopOrgId(LzljOrg org) {
        if (org.getParentId() == null || org.getParentId() == 0) {
            return org.getId();
        }
        // 递归获取顶层
        String path = org.getLevelPath();
        if (StringUtils.hasText(path)) {
            String[] parts = path.split("/");
            if (parts.length > 1) {
                return Long.parseLong(parts[1]);
            }
        }
        return org.getId();
    }

    /**
     * 解析场景ID列表
     */
    private List<Long> parseScenarioIds(String scenarioIds) {
        if (!StringUtils.hasText(scenarioIds)) {
            return new ArrayList<>();
        }
        try {
            return JSON.parseArray(scenarioIds, Long.class);
        } catch (Exception e) {
            log.warn("解析scenario_ids失败: {}", scenarioIds);
            return new ArrayList<>();
        }
    }

    private MerchantDTO convertToMerchantDTO(LzljMerchant merchant) {
        MerchantDTO dto = new MerchantDTO();
        BeanUtils.copyProperties(merchant, dto);
        return dto;
    }

    private SettlementInfoDTO convertToSettlementDTO(LzljSettlementInfo settlement) {
        SettlementInfoDTO dto = new SettlementInfoDTO();
        BeanUtils.copyProperties(settlement, dto);
        return dto;
    }
}
