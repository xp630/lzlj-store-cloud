package com.lzlj.account.systemparameter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.systemparameter.dao.SystemParameterDao;
import com.lzlj.account.systemparameter.dto.CreateSystemParameterDTO;
import com.lzlj.account.systemparameter.dto.SystemParameterDTO;
import com.lzlj.account.systemparameter.dto.SystemParameterQueryDTO;
import com.lzlj.account.systemparameter.dto.UpdateSystemParameterDTO;
import com.lzlj.account.systemparameter.entity.SystemParameter;
import com.lzlj.account.systemparameter.service.SystemParameterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统参数服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemParameterServiceImpl implements SystemParameterService {

    private final SystemParameterDao systemParameterDao;

    @Override
    public Long create(CreateSystemParameterDTO dto) {
        // 检查参数编码唯一性
        if (checkKeyExists(dto.getParamKey(), null)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "参数编码已存在");
        }

        // 验证参数值格式
        validateParamValue(dto.getParamValue(), dto.getParamType());

        SystemParameter param = new SystemParameter();
        BeanUtils.copyProperties(dto, param);
        systemParameterDao.insert(param);
        log.info("创建系统参数成功: id={}, paramKey={}", param.getId(), param.getParamKey());
        return param.getId();
    }

    @Override
    public void update(Long id, UpdateSystemParameterDTO dto) {
        SystemParameter existParam = systemParameterDao.selectById(id);
        if (existParam == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查参数编码唯一性（排除自身）
        if (!dto.getParamKey().equals(existParam.getParamKey())) {
            if (checkKeyExists(dto.getParamKey(), id)) {
                throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "参数编码已存在");
            }
        }

        // 验证参数值格式
        validateParamValue(dto.getParamValue(), dto.getParamType());

        BeanUtils.copyProperties(dto, existParam);
        systemParameterDao.updateById(existParam);
        log.info("更新系统参数成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        SystemParameter param = systemParameterDao.selectById(id);
        if (param == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        systemParameterDao.deleteById(id);
        log.info("删除系统参数成功: id={}", id);
    }

    @Override
    public SystemParameterDTO getById(Long id) {
        SystemParameter param = systemParameterDao.selectById(id);
        if (param == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(param);
    }

    @Override
    public SystemParameterDTO getByKey(String key) {
        LambdaQueryWrapper<SystemParameter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemParameter::getParamKey, key);
        SystemParameter param = systemParameterDao.selectOne(wrapper);
        if (param == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(param);
    }

    @Override
    public PageResult<SystemParameterDTO> page(SystemParameterQueryDTO query, Integer pageNum, Integer pageSize) {
        Page<SystemParameter> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SystemParameter> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getParamName()), SystemParameter::getParamName, query.getParamName())
               .eq(query.getStatus() != null, SystemParameter::getStatus, query.getStatus())
               .orderByDesc(SystemParameter::getCreateTime);

        IPage<SystemParameter> resultPage = systemParameterDao.selectPage(page, wrapper);

        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public List<SystemParameterDTO> list() {
        LambdaQueryWrapper<SystemParameter> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SystemParameter::getCreateTime);
        List<SystemParameter> params = systemParameterDao.selectList(wrapper);
        return params.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private boolean checkKeyExists(String paramKey, Long excludeId) {
        LambdaQueryWrapper<SystemParameter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemParameter::getParamKey, paramKey);
        if (excludeId != null) {
            wrapper.ne(SystemParameter::getId, excludeId);
        }
        return systemParameterDao.selectCount(wrapper) > 0;
    }

    private void validateParamValue(String value, String paramType) {
        if (value == null || value.isEmpty()) {
            return; // 空值不校验
        }
        try {
            switch (paramType) {
                case "INTEGER":
                    Integer.parseInt(value);
                    break;
                case "BOOLEAN":
                    if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                        throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "参数值格式不正确");
                    }
                    break;
                case "DECIMAL":
                    new java.math.BigDecimal(value);
                    break;
                case "STRING":
                default:
                    break;
            }
        } catch (NumberFormatException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "参数值格式不正确");
        }
    }

    private SystemParameterDTO convertToDTO(SystemParameter param) {
        SystemParameterDTO dto = new SystemParameterDTO();
        BeanUtils.copyProperties(param, dto);
        return dto;
    }
}
