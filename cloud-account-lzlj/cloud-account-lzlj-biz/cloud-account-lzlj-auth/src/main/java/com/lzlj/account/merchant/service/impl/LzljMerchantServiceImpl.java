package com.lzlj.account.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.domain.merchant.MerchantChannelAccountDTO;
import com.lzlj.account.common.core.domain.merchant.MerchantLegalDTO;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.merchant.dao.LzljMerchantChannelAccountDao;
import com.lzlj.account.merchant.dao.LzljMerchantDao;
import com.lzlj.account.merchant.dao.LzljMerchantLegalDao;
import com.lzlj.account.merchant.dao.LzljMerchantUserDao;
import com.lzlj.account.merchant.dao.LzljSettlementInfoDao;
import com.lzlj.account.merchant.dto.*;
import com.lzlj.account.merchant.entity.LzljMerchant;
import com.lzlj.account.merchant.entity.LzljMerchantChannelAccount;
import com.lzlj.account.merchant.entity.LzljMerchantLegal;
import com.lzlj.account.merchant.entity.LzljMerchantUser;
import com.lzlj.account.merchant.entity.LzljSettlementInfo;
import com.lzlj.account.merchant.service.LzljMerchantService;
import com.lzlj.account.scenario.dao.LzljScenarioDao;
import com.lzlj.account.scenario.entity.LzljScenario;
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
import java.util.Collections;
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
    private final LzljMerchantLegalDao merchantLegalDao;
    private final LzljMerchantChannelAccountDao merchantChannelAccountDao;
    private final LzljOrgDao orgDao;
    private final LzljScenarioDao scenarioDao;
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
            updateMerchantFromSync(existMerchant, dto);
            merchantDao.updateById(existMerchant);
            return getById(existMerchant.getId());
        }

        // 创建商户
        LzljMerchant merchant = new LzljMerchant();
        BeanUtils.copyProperties(dto, merchant);
        merchant.setMerchantCode(dto.getMerchantCode()); // 同步时使用传入的code
        merchant.setStatus(1);
        // 默认设置为母户类型
        if (merchant.getMerchantType() == null) {
            merchant.setMerchantType(1);
        }
        // 设置场景codes
        if (dto.getScenarioCodes() != null && !dto.getScenarioCodes().isEmpty()) {
            merchant.setScenarioCodes(toJsonString(dto.getScenarioCodes()));
        }
        merchantDao.insert(merchant);

        // 创建法人信息
        saveMerchantLegal(merchant.getId(), dto.getLegal());

        // 创建银联账户
        saveMerchantChannelAccounts(merchant.getId(), dto.getChannelAccounts());

        // 创建结算信息（空）
        LzljSettlementInfo settlement = new LzljSettlementInfo();
        settlement.setMerchantId(merchant.getId());
        settlement.setStatus(1);
        settlementDao.insert(settlement);

        // 创建母户机构
        LzljOrg org = createMerchantOrg(merchant, null, null);

        MerchantDTO result = convertToMerchantDTO(merchant);
        result.setOrgId(org.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantDTO create(CreateMerchantDTO dto) {
        // 子户校验
        if (dto.getMerchantType() != null && dto.getMerchantType() == 2) {
            // 子户必须指定母户
            if (dto.getParentId() == null) {
                throw new BusinessException(ResultCode.FAIL.getCode(), "子户必须指定母户");
            }
            // 校验母户存在且为母户类型
            LzljMerchant parentMerchant = merchantDao.selectById(dto.getParentId());
            if (parentMerchant == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "母户不存在");
            }
            if (parentMerchant.getMerchantType() == null || parentMerchant.getMerchantType() != 1) {
                throw new BusinessException(ResultCode.FAIL.getCode(), "指定的母户不是母户类型");
            }
            // 子户必须指定场景
            if (dto.getScenarioId() == null) {
                throw new BusinessException(ResultCode.FAIL.getCode(), "子户必须指定业务场景");
            }
            // 校验场景存在
            LzljScenario scenario = scenarioDao.selectById(dto.getScenarioId());
            if (scenario == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "业务场景不存在");
            }
        }

        LzljMerchant merchant = new LzljMerchant();
        BeanUtils.copyProperties(dto, merchant);
        merchant.setMerchantCode(generateMerchantCode());
        merchant.setStatus(1);
        // 设置场景codes（母户）
        if (dto.getScenarioCodes() != null && !dto.getScenarioCodes().isEmpty()) {
            merchant.setScenarioCodes(toJsonString(dto.getScenarioCodes()));
        }
        merchantDao.insert(merchant);

        // 保存法人信息
        saveMerchantLegal(merchant.getId(), dto.getLegal());

        // 保存银联账户
        saveMerchantChannelAccounts(merchant.getId(), dto.getChannelAccounts());

        // 创建结算信息（空）
        LzljSettlementInfo settlement = new LzljSettlementInfo();
        settlement.setMerchantId(merchant.getId());
        settlement.setStatus(1);
        settlementDao.insert(settlement);

        // 子户才创建机构
        Long orgId = null;
        if (merchant.getMerchantType() == 2) {
            LzljOrg org = createMerchantOrg(merchant, dto.getParentId(), dto.getScenarioId());
            orgId = org.getId();
        }

        MerchantDTO result = convertToMerchantDTO(merchant);
        result.setOrgId(orgId);
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
        if (dto.getStatus() != null) {
            merchant.setStatus(dto.getStatus());
        }
        if (dto.getScenarioCodes() != null) {
            merchant.setScenarioCodes(toJsonString(dto.getScenarioCodes()));
        }
        if (dto.getScenarioId() != null) {
            merchant.setScenarioId(dto.getScenarioId());
        }

        merchantDao.updateById(merchant);

        // 更新法人信息
        if (dto.getLegal() != null) {
            saveMerchantLegal(id, dto.getLegal());
        }

        // 更新银联账户
        if (dto.getChannelAccounts() != null) {
            // 先删除旧的，再保存新的
            LambdaQueryWrapper<LzljMerchantChannelAccount> delWrapper = new LambdaQueryWrapper<>();
            delWrapper.eq(LzljMerchantChannelAccount::getMerchantId, id);
            merchantChannelAccountDao.delete(delWrapper);
            saveMerchantChannelAccounts(id, dto.getChannelAccounts());
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

        // 软删除法人信息
        LambdaQueryWrapper<LzljMerchantLegal> legalWrapper = new LambdaQueryWrapper<>();
        legalWrapper.eq(LzljMerchantLegal::getMerchantId, id);
        merchantLegalDao.delete(legalWrapper);

        // 软删除银联账户
        LambdaQueryWrapper<LzljMerchantChannelAccount> channelWrapper = new LambdaQueryWrapper<>();
        channelWrapper.eq(LzljMerchantChannelAccount::getMerchantId, id);
        merchantChannelAccountDao.delete(channelWrapper);

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

        // 查询商户机构
        LambdaQueryWrapper<LzljOrg> orgWrapper = new LambdaQueryWrapper<>();
        orgWrapper.eq(LzljOrg::getMerchantId, id)
                  .eq(LzljOrg::getDeleted, 0);
        LzljOrg org = orgDao.selectOne(orgWrapper);
        if (org != null) {
            dto.setOrgId(org.getId());
        }

        // 查询法人信息
        LambdaQueryWrapper<LzljMerchantLegal> legalWrapper = new LambdaQueryWrapper<>();
        legalWrapper.eq(LzljMerchantLegal::getMerchantId, id)
                   .eq(LzljMerchantLegal::getDeleted, 0);
        LzljMerchantLegal legal = merchantLegalDao.selectOne(legalWrapper);
        if (legal != null) {
            MerchantLegalDTO legalDTO = new MerchantLegalDTO();
            BeanUtils.copyProperties(legal, legalDTO);
            dto.setLegal(legalDTO);
        }

        // 查询银联账户
        LambdaQueryWrapper<LzljMerchantChannelAccount> channelWrapper = new LambdaQueryWrapper<>();
        channelWrapper.eq(LzljMerchantChannelAccount::getMerchantId, id)
                     .eq(LzljMerchantChannelAccount::getDeleted, 0);
        List<LzljMerchantChannelAccount> channelAccounts = merchantChannelAccountDao.selectList(channelWrapper);
        if (channelAccounts != null && !channelAccounts.isEmpty()) {
            dto.setChannelAccounts(channelAccounts.stream().map(ca -> {
                MerchantChannelAccountDTO caDTO = new MerchantChannelAccountDTO();
                BeanUtils.copyProperties(ca, caDTO);
                // 查询渠道名称
                if (ca.getChannelId() != null) {
                    LzljScenario channel = scenarioDao.selectById(ca.getChannelId());
                    if (channel != null) {
                        caDTO.setChannelName(channel.getScenarioName());
                    }
                }
                return caDTO;
            }).collect(Collectors.toList()));
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

        return topOrg.getScenarioId() != null ? Collections.singletonList(topOrg.getScenarioId()) : new ArrayList<>();
    }

    // ==================== 私有方法 ====================

    /**
     * 保存商户法人信息
     */
    private void saveMerchantLegal(Long merchantId, MerchantLegalDTO legalDTO) {
        if (legalDTO == null) {
            return;
        }
        // 先删除旧的
        LambdaQueryWrapper<LzljMerchantLegal> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(LzljMerchantLegal::getMerchantId, merchantId);
        merchantLegalDao.delete(delWrapper);

        // 新增
        LzljMerchantLegal legal = new LzljMerchantLegal();
        BeanUtils.copyProperties(legalDTO, legal);
        legal.setId(null); // 确保ID为null，由数据库生成
        legal.setMerchantId(merchantId); // 在copyProperties之后设置
        legal.setStatus(1);
        merchantLegalDao.insert(legal);
    }

    /**
     * 保存商户银联账户
     */
    private void saveMerchantChannelAccounts(Long merchantId, List<MerchantChannelAccountDTO> channelAccounts) {
        if (channelAccounts == null || channelAccounts.isEmpty()) {
            return;
        }
        // 先删除旧的
        LambdaQueryWrapper<LzljMerchantChannelAccount> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(LzljMerchantChannelAccount::getMerchantId, merchantId);
        merchantChannelAccountDao.delete(delWrapper);

        // 新增
        for (MerchantChannelAccountDTO caDTO : channelAccounts) {
            LzljMerchantChannelAccount ca = new LzljMerchantChannelAccount();
            BeanUtils.copyProperties(caDTO, ca);
            ca.setId(null); // 确保ID为null，由数据库生成
            ca.setMerchantId(merchantId); // 在copyProperties之后设置
            ca.setStatus(1);
            merchantChannelAccountDao.insert(ca);
        }
    }

    /**
     * 从同步DTO更新商户
     */
    private void updateMerchantFromSync(LzljMerchant merchant, SyncMerchantDTO dto) {
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
        if (dto.getScenarioCodes() != null) {
            merchant.setScenarioCodes(toJsonString(dto.getScenarioCodes()));
        }
        merchant.setStatus(1);

        // 更新法人信息
        saveMerchantLegal(merchant.getId(), dto.getLegal());

        // 更新银联账户
        if (dto.getChannelAccounts() != null) {
            LambdaQueryWrapper<LzljMerchantChannelAccount> delWrapper = new LambdaQueryWrapper<>();
            delWrapper.eq(LzljMerchantChannelAccount::getMerchantId, merchant.getId());
            merchantChannelAccountDao.delete(delWrapper);
            saveMerchantChannelAccounts(merchant.getId(), dto.getChannelAccounts());
        }
    }

    /**
     * 创建商户对应的机构
     */
    private LzljOrg createMerchantOrg(LzljMerchant merchant, Long parentMerchantId, Long scenarioId) {
        LzljOrg org = new LzljOrg();
        org.setOrgCode("ORG-" + merchant.getMerchantCode());
        org.setOrgName(merchant.getMerchantName());
        org.setMerchantId(merchant.getId());
        org.setProvinceCode(merchant.getProvinceCode());
        org.setCityCode(merchant.getCityCode());
        org.setDistrictCode(merchant.getDistrictCode());
        org.setAddress(merchant.getAddress());
        org.setContact(merchant.getContact());
        org.setContactPhone(merchant.getContactPhone());
        org.setStatus(1);

        if (merchant.getMerchantType() == 1) {
            // 母户：作为根节点
            org.setOrgType(2);  // 账户级别
            org.setParentId(0L);
            org.setLevel(1);
            org.setLevelPath("/");
        } else {
            // 子户：挂在场景机构下
            org.setOrgType(2);  // 账户级别
            org.setScenarioId(scenarioId);

            // 查找场景机构作为父节点
            LambdaQueryWrapper<LzljOrg> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LzljOrg::getScenarioId, scenarioId)
                   .eq(LzljOrg::getMerchantId, parentMerchantId)
                   .eq(LzljOrg::getDeleted, 0);
            LzljOrg parentOrg = orgDao.selectOne(wrapper);
            if (parentOrg == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND, "未找到对应的场景机构");
            }
            org.setParentId(parentOrg.getId());
            org.setLevel(parentOrg.getLevel() + 1);
            org.setLevelPath(parentOrg.getLevelPath());
        }

        orgDao.insert(org);

        // 回填 level_path
        org.setLevelPath(org.getLevelPath() + org.getId() + "/");
        orgDao.updateById(org);

        return org;
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

    private MerchantDTO convertToMerchantDTO(LzljMerchant merchant) {
        MerchantDTO dto = new MerchantDTO();
        BeanUtils.copyProperties(merchant, dto);

        // 解析 scenarioCodes（存储为逗号分隔字符串）
        if (StringUtils.hasText(merchant.getScenarioCodes())) {
            List<String> scenarioCodes = Arrays.asList(merchant.getScenarioCodes().split(","));
            dto.setScenarioCodes(scenarioCodes);
        }

        return dto;
    }

    private SettlementInfoDTO convertToSettlementDTO(LzljSettlementInfo settlement) {
        SettlementInfoDTO dto = new SettlementInfoDTO();
        BeanUtils.copyProperties(settlement, dto);
        return dto;
    }

    private String toJsonString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return String.join(",", list);
    }
}
