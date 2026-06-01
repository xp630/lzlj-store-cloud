package com.lzlj.account.scenario.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.merchant.dao.LzljMerchantDao;
import com.lzlj.account.merchant.entity.LzljMerchant;
import com.lzlj.account.scenario.dao.LzljScenarioChannelDao;
import com.lzlj.account.scenario.dao.LzljScenarioDao;
import com.lzlj.account.scenario.dto.CreateScenarioDTO;
import com.lzlj.account.scenario.dto.ScenarioDTO;
import com.lzlj.account.scenario.dto.ScenarioQueryDTO;
import com.lzlj.account.scenario.dto.UpdateScenarioDTO;
import com.lzlj.account.scenario.entity.LzljScenario;
import com.lzlj.account.scenario.entity.LzljScenarioChannel;
import com.lzlj.account.scenario.service.LzljScenarioService;
import com.lzlj.account.user.dao.LzljOrgDao;
import com.lzlj.account.user.entity.LzljOrg;
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
 * LZLJ 业务场景服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljScenarioServiceImpl implements LzljScenarioService {

    private final LzljScenarioDao scenarioDao;
    private final LzljScenarioChannelDao scenarioChannelDao;
    private final LzljMerchantDao merchantDao;
    private final LzljOrgDao orgDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScenarioDTO create(CreateScenarioDTO dto) {
        // 校验母商户存在且为母户类型
        LzljMerchant merchant = merchantDao.selectById(dto.getMerchantId());
        if (merchant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "商户不存在");
        }
        if (merchant.getMerchantType() == null || merchant.getMerchantType() != 1) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "只能为母户创建场景");
        }

        // 检查场景代码唯一性（同一母户下唯一）
        LambdaQueryWrapper<LzljScenario> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljScenario::getMerchantId, dto.getMerchantId())
               .eq(LzljScenario::getScenarioCode, dto.getScenarioCode())
               .eq(LzljScenario::getDeleted, 0);
        if (scenarioDao.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS, "该母户下场景代码已存在");
        }

        // 创建场景
        LzljScenario scenario = new LzljScenario();
        BeanUtils.copyProperties(dto, scenario);
        if (scenario.getSort() == null) {
            scenario.setSort(0);
        }
        if (scenario.getStatus() == null) {
            scenario.setStatus(1);
        }
        scenarioDao.insert(scenario);

        // 查找母户机构作为父节点
        LambdaQueryWrapper<LzljOrg> orgWrapper = new LambdaQueryWrapper<>();
        orgWrapper.eq(LzljOrg::getMerchantId, merchant.getId())
                 .eq(LzljOrg::getDeleted, 0);
        LzljOrg parentOrg = orgDao.selectOne(orgWrapper);

        // 同步创建机构（挂在母户机构下）
        LzljOrg org = new LzljOrg();
        org.setOrgCode(scenario.getScenarioCode());
        org.setOrgName(scenario.getScenarioName());
        org.setOrgType(1);  // 1:总部级别（业务场景）
        org.setMerchantId(merchant.getId());
        org.setScenarioId(scenario.getId());
        org.setStatus(1);
        org.setSort(scenario.getSort());

        if (parentOrg != null) {
            org.setParentId(parentOrg.getId());
            org.setLevel(parentOrg.getLevel() + 1);
            org.setLevelPath(parentOrg.getLevelPath());
        } else {
            org.setParentId(0L);
            org.setLevel(1);
            org.setLevelPath("/");
        }
        orgDao.insert(org);

        // 回填 level_path
        org.setLevelPath(org.getLevelPath() + org.getId() + "/");
        orgDao.updateById(org);

        // 保存场景与通道关联
        saveScenarioChannels(scenario.getId(), dto.getChannelIds());

        return getById(scenario.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, UpdateScenarioDTO dto) {
        LzljScenario scenario = scenarioDao.selectById(id);
        if (scenario == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        if (StringUtils.hasText(dto.getScenarioName())) {
            scenario.setScenarioName(dto.getScenarioName());
        }
        if (dto.getDescription() != null) {
            scenario.setDescription(dto.getDescription());
        }
        if (dto.getIcon() != null) {
            scenario.setIcon(dto.getIcon());
        }
        if (dto.getSort() != null) {
            scenario.setSort(dto.getSort());
        }
        if (dto.getStatus() != null) {
            scenario.setStatus(dto.getStatus());
        }
        scenarioDao.updateById(scenario);

        // 同步更新机构名称
        LambdaQueryWrapper<LzljOrg> orgWrapper = new LambdaQueryWrapper<>();
        orgWrapper.eq(LzljOrg::getScenarioId, id);
        LzljOrg org = orgDao.selectOne(orgWrapper);
        if (org != null) {
            if (StringUtils.hasText(dto.getScenarioName())) {
                org.setOrgName(dto.getScenarioName());
            }
            org.setSort(scenario.getSort());
            orgDao.updateById(org);
        }

        // 更新场景与通道关联
        if (dto.getChannelIds() != null) {
            // 删除旧关联
            LambdaQueryWrapper<LzljScenarioChannel> delWrapper = new LambdaQueryWrapper<>();
            delWrapper.eq(LzljScenarioChannel::getScenarioId, id);
            scenarioChannelDao.delete(delWrapper);

            // 保存新关联
            saveScenarioChannels(id, dto.getChannelIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        LzljScenario scenario = scenarioDao.selectById(id);
        if (scenario == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 软删除场景
        scenarioDao.deleteById(id);

        // 软删除场景与通道关联
        LambdaQueryWrapper<LzljScenarioChannel> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(LzljScenarioChannel::getScenarioId, id);
        scenarioChannelDao.delete(delWrapper);
    }

    @Override
    public ScenarioDTO getById(Long id) {
        LzljScenario scenario = scenarioDao.selectById(id);
        if (scenario == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(scenario);
    }

    @Override
    public PageResult<ScenarioDTO> page(ScenarioQueryDTO query) {
        LambdaQueryWrapper<LzljScenario> wrapper = new LambdaQueryWrapper<>();
        if (query.getMerchantId() != null) {
            wrapper.eq(LzljScenario::getMerchantId, query.getMerchantId());
        }
        if (StringUtils.hasText(query.getScenarioCode())) {
            wrapper.eq(LzljScenario::getScenarioCode, query.getScenarioCode());
        }
        if (StringUtils.hasText(query.getScenarioName())) {
            wrapper.like(LzljScenario::getScenarioName, query.getScenarioName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(LzljScenario::getStatus, query.getStatus());
        }
        wrapper.eq(LzljScenario::getDeleted, 0)
               .orderByAsc(LzljScenario::getSort)
               .orderByDesc(LzljScenario::getCreateTime);

        IPage<LzljScenario> page = scenarioDao.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()),
                wrapper
        );

        List<ScenarioDTO> records = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageResult<>(records, page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Override
    public List<ScenarioDTO> listEnabled() {
        LambdaQueryWrapper<LzljScenario> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljScenario::getStatus, 1)
               .eq(LzljScenario::getDeleted, 0)
               .orderByAsc(LzljScenario::getSort);

        return scenarioDao.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getChannelIds(Long scenarioId) {
        LambdaQueryWrapper<LzljScenarioChannel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljScenarioChannel::getScenarioId, scenarioId)
               .eq(LzljScenarioChannel::getDeleted, 0)
               .eq(LzljScenarioChannel::getStatus, 1);

        return scenarioChannelDao.selectList(wrapper).stream()
                .map(LzljScenarioChannel::getChannelId)
                .collect(Collectors.toList());
    }

    /**
     * 保存场景与通道关联
     */
    private void saveScenarioChannels(Long scenarioId, List<Long> channelIds) {
        if (channelIds == null || channelIds.isEmpty()) {
            return;
        }
        for (Long channelId : channelIds) {
            LzljScenarioChannel sc = new LzljScenarioChannel();
            sc.setScenarioId(scenarioId);
            sc.setChannelId(channelId);
            sc.setStatus(1);
            scenarioChannelDao.insert(sc);
        }
    }

    /**
     * 转换DTO
     */
    private ScenarioDTO convertToDTO(LzljScenario scenario) {
        ScenarioDTO dto = new ScenarioDTO();
        BeanUtils.copyProperties(scenario, dto);

        // 查询母户名称
        if (scenario.getMerchantId() != null) {
            LzljMerchant merchant = merchantDao.selectById(scenario.getMerchantId());
            if (merchant != null) {
                dto.setMerchantName(merchant.getMerchantName());
            }
        }

        dto.setChannelIds(getChannelIds(scenario.getId()));
        return dto;
    }
}
