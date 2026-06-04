package com.lzlj.account.datarole.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.datarole.dao.DataRoleConditionDao;
import com.lzlj.account.datarole.dao.DataRoleDao;
import com.lzlj.account.datarole.dao.UserDataRoleDao;
import com.lzlj.account.datarole.dto.*;
import com.lzlj.account.datarole.entity.DataRole;
import com.lzlj.account.datarole.entity.DataRoleCondition;
import com.lzlj.account.datarole.entity.UserDataRole;
import com.lzlj.account.datarole.service.DataRoleService;
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
 * 数据角色服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataRoleServiceImpl implements DataRoleService {

    private final DataRoleDao dataRoleDao;
    private final DataRoleConditionDao conditionDao;
    private final UserDataRoleDao userDataRoleDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataRoleDTO create(CreateDataRoleDTO dto) {
        DataRole dataRole = new DataRole();
        dataRole.setDataRoleName(dto.getDataRoleName());
        dataRole.setDataRoleCode(dto.getDataRoleCode());
        dataRole.setDescription(dto.getDescription());
        dataRole.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        dataRoleDao.insert(dataRole);

        // 保存条件
        saveConditions(dataRole.getId(), dto.getConditions());

        return getById(dataRole.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataRoleDTO update(Long id, UpdateDataRoleDTO dto) {
        DataRole dataRole = dataRoleDao.selectById(id);
        if (dataRole == null) {
            throw new RuntimeException("数据角色不存在");
        }

        dataRole.setDataRoleName(dto.getDataRoleName());
        dataRole.setDescription(dto.getDescription());
        dataRole.setStatus(dto.getStatus());
        dataRoleDao.updateById(dataRole);

        // 先删除旧条件，再保存新条件
        conditionDao.delete(new LambdaQueryWrapper<DataRoleCondition>()
                .eq(DataRoleCondition::getDataRoleId, id));
        saveConditions(id, dto.getConditions());

        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 删除条件
        conditionDao.delete(new LambdaQueryWrapper<DataRoleCondition>()
                .eq(DataRoleCondition::getDataRoleId, id));
        // 删除用户关联
        userDataRoleDao.delete(new LambdaQueryWrapper<UserDataRole>()
                .eq(UserDataRole::getDataRoleId, id));
        // 删除角色
        dataRoleDao.deleteById(id);
    }

    @Override
    public DataRoleDTO getById(Long id) {
        DataRole dataRole = dataRoleDao.selectById(id);
        if (dataRole == null) {
            return null;
        }

        DataRoleDTO dto = new DataRoleDTO();
        BeanUtils.copyProperties(dataRole, dto);

        // 查询条件列表
        List<DataRoleCondition> conditions = conditionDao.selectList(
                new LambdaQueryWrapper<DataRoleCondition>()
                        .eq(DataRoleCondition::getDataRoleId, id)
                        .orderByAsc(DataRoleCondition::getConditionGroup)
                        .orderByAsc(DataRoleCondition::getSort)
        );
        dto.setConditions(conditions.stream().map(this::toConditionDTO).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public PageResult<DataRoleDTO> page(DataRoleQueryDTO query) {
        Page<DataRole> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<DataRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getDataRoleName())) {
            wrapper.like(DataRole::getDataRoleName, query.getDataRoleName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(DataRole::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(DataRole::getCreateTime);

        IPage<DataRole> pageResult = dataRoleDao.selectPage(page, wrapper);

        List<DataRoleDTO> records = pageResult.getRecords().stream()
                .map(role -> {
                    DataRoleDTO dto = new DataRoleDTO();
                    BeanUtils.copyProperties(role, dto);
                    return dto;
                })
                .collect(Collectors.toList());

        PageResult<DataRoleDTO> result = new PageResult<>();
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setPages(pageResult.getPages());
        result.setRecords(records);
        result.setHasPrevious(pageResult.getCurrent() > 1);
        result.setHasNext(pageResult.getCurrent() < pageResult.getPages());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(AssignDataRoleDTO dto) {
        Long userId = dto.getUserId();
        List<Long> dataRoleIds = dto.getDataRoleIds();

        // 先删除旧关联
        userDataRoleDao.delete(new LambdaQueryWrapper<UserDataRole>()
                .eq(UserDataRole::getUserId, userId));

        // 插入新关联
        for (Long dataRoleId : dataRoleIds) {
            UserDataRole userDataRole = new UserDataRole();
            userDataRole.setUserId(userId);
            userDataRole.setDataRoleId(dataRoleId);
            userDataRoleDao.insert(userDataRole);
        }
    }

    @Override
    public List<DataRoleDTO> getUserDataRoles(Long userId) {
        // 查询用户关联的数据角色ID
        List<UserDataRole> userDataRoles = userDataRoleDao.selectList(
                new LambdaQueryWrapper<UserDataRole>()
                        .eq(UserDataRole::getUserId, userId)
        );

        if (userDataRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> dataRoleIds = userDataRoles.stream()
                .map(UserDataRole::getDataRoleId)
                .collect(Collectors.toList());

        // 查询数据角色
        List<DataRole> dataRoles = dataRoleDao.selectList(
                new LambdaQueryWrapper<DataRole>()
                        .in(DataRole::getId, dataRoleIds)
                        .eq(DataRole::getStatus, 1)
        );

        return dataRoles.stream().map(role -> {
            DataRoleDTO dto = new DataRoleDTO();
            BeanUtils.copyProperties(role, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<DataRoleCondition> getUserDataRoleConditions(Long userId) {
        // 查询用户启用的数据角色ID
        List<UserDataRole> userDataRoles = userDataRoleDao.selectList(
                new LambdaQueryWrapper<UserDataRole>()
                        .eq(UserDataRole::getUserId, userId)
        );

        if (userDataRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> dataRoleIds = userDataRoles.stream()
                .map(UserDataRole::getDataRoleId)
                .collect(Collectors.toList());

        // 查询启用的数据角色的条件
        List<DataRole> enabledRoles = dataRoleDao.selectList(
                new LambdaQueryWrapper<DataRole>()
                        .in(DataRole::getId, dataRoleIds)
                        .eq(DataRole::getStatus, 1)
        );

        if (enabledRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> enabledRoleIds = enabledRoles.stream()
                .map(DataRole::getId)
                .collect(Collectors.toList());

        return conditionDao.selectList(
                new LambdaQueryWrapper<DataRoleCondition>()
                        .in(DataRoleCondition::getDataRoleId, enabledRoleIds)
                        .orderByAsc(DataRoleCondition::getConditionGroup)
                        .orderByAsc(DataRoleCondition::getSort)
        );
    }

    @Override
    public List<DataRoleCondition> getCurrentUserDataRoleConditions() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return new ArrayList<>();
        }
        return getUserDataRoleConditions(userId);
    }

    /**
     * 保存条件列表
     */
    private void saveConditions(Long dataRoleId, List<ConditionDTO> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return;
        }

        for (int i = 0; i < conditions.size(); i++) {
            ConditionDTO condDto = conditions.get(i);
            DataRoleCondition condition = new DataRoleCondition();
            condition.setDataRoleId(dataRoleId);
            condition.setFieldName(condDto.getFieldName());
            condition.setOperator(condDto.getOperator());
            condition.setValueType(condDto.getValueType());
            condition.setFieldValue(condDto.getFieldValue());
            condition.setDynamicValueKey(condDto.getDynamicValueKey());
            condition.setLogicalOperator(condDto.getLogicalOperator());
            condition.setConditionGroup(condDto.getConditionGroup() != null ? condDto.getConditionGroup() : 1);
            condition.setSort(condDto.getSort() != null ? condDto.getSort() : i + 1);
            conditionDao.insert(condition);
        }
    }

    /**
     * 转换为ConditionDTO
     */
    private ConditionDTO toConditionDTO(DataRoleCondition condition) {
        ConditionDTO dto = new ConditionDTO();
        dto.setId(condition.getId());
        dto.setFieldName(condition.getFieldName());
        dto.setOperator(condition.getOperator());
        dto.setValueType(condition.getValueType());
        dto.setFieldValue(condition.getFieldValue());
        dto.setDynamicValueKey(condition.getDynamicValueKey());
        dto.setLogicalOperator(condition.getLogicalOperator());
        dto.setConditionGroup(condition.getConditionGroup());
        dto.setSort(condition.getSort());
        return dto;
    }
}
