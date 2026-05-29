package com.lzlj.account.systemparameter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.systemparameter.dao.LzljSystemParameterDao;
import com.lzlj.account.systemparameter.dto.*;
import com.lzlj.account.systemparameter.entity.LzljSystemParameter;
import com.lzlj.account.systemparameter.service.LzljSystemParameterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LzljSystemParameterServiceImpl implements LzljSystemParameterService {

    private final LzljSystemParameterDao lzljSystemParameterDao;

    @Override
    public Long create(CreateLzljSystemParameterDTO dto) {
        if (checkKeyExists(dto.getParamKey(), null)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "参数编码已存在");
        }
        validateParamValue(dto.getParamValue(), dto.getParamType());
        LzljSystemParameter param = new LzljSystemParameter();
        BeanUtils.copyProperties(dto, param);
        lzljSystemParameterDao.insert(param);
        log.info("创建LZLJ系统参数成功: id={}, paramKey={}", param.getId(), param.getParamKey());
        return param.getId();
    }

    @Override
    public void update(Long id, UpdateLzljSystemParameterDTO dto) {
        LzljSystemParameter existParam = lzljSystemParameterDao.selectById(id);
        if (existParam == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (!dto.getParamKey().equals(existParam.getParamKey())) {
            if (checkKeyExists(dto.getParamKey(), id)) {
                throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "参数编码已存在");
            }
        }
        validateParamValue(dto.getParamValue(), dto.getParamType());
        BeanUtils.copyProperties(dto, existParam);
        lzljSystemParameterDao.updateById(existParam);
        log.info("更新LZLJ系统参数成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        LzljSystemParameter param = lzljSystemParameterDao.selectById(id);
        if (param == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        lzljSystemParameterDao.deleteById(id);
        log.info("删除LZLJ系统参数成功: id={}", id);
    }

    @Override
    public LzljSystemParameterDTO getById(Long id) {
        LzljSystemParameter param = lzljSystemParameterDao.selectById(id);
        if (param == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(param);
    }

    @Override
    public LzljSystemParameterDTO getByKey(String key) {
        LambdaQueryWrapper<LzljSystemParameter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljSystemParameter::getParamKey, key);
        LzljSystemParameter param = lzljSystemParameterDao.selectOne(wrapper);
        if (param == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(param);
    }

    @Override
    public PageResult<LzljSystemParameterDTO> page(LzljSystemParameterQueryDTO query, Integer pageNum, Integer pageSize) {
        Page<LzljSystemParameter> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LzljSystemParameter> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getParamName()), LzljSystemParameter::getParamName, query.getParamName())
               .eq(query.getStatus() != null, LzljSystemParameter::getStatus, query.getStatus())
               .orderByDesc(LzljSystemParameter::getCreateTime);
        IPage<LzljSystemParameter> resultPage = lzljSystemParameterDao.selectPage(page, wrapper);
        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public List<LzljSystemParameterDTO> list() {
        LambdaQueryWrapper<LzljSystemParameter> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(LzljSystemParameter::getCreateTime);
        List<LzljSystemParameter> params = lzljSystemParameterDao.selectList(wrapper);
        return params.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private boolean checkKeyExists(String paramKey, Long excludeId) {
        LambdaQueryWrapper<LzljSystemParameter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljSystemParameter::getParamKey, paramKey);
        if (excludeId != null) {
            wrapper.ne(LzljSystemParameter::getId, excludeId);
        }
        return lzljSystemParameterDao.selectCount(wrapper) > 0;
    }

    private void validateParamValue(String value, String paramType) {
        if (value == null || value.isEmpty()) return;
        try {
            switch (paramType) {
                case "INTEGER": Integer.parseInt(value); break;
                case "BOOLEAN":
                    if (!"true".equalsIgnoreCase(value) && !"false".equalsIgnoreCase(value)) {
                        throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "参数值格式不正确");
                    }
                    break;
                case "DECIMAL": new java.math.BigDecimal(value); break;
            }
        } catch (NumberFormatException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "参数值格式不正确");
        }
    }

    private LzljSystemParameterDTO convertToDTO(LzljSystemParameter param) {
        LzljSystemParameterDTO dto = new LzljSystemParameterDTO();
        BeanUtils.copyProperties(param, dto);
        return dto;
    }
}
