package com.lzlj.account.datadictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.datadictionary.dao.DataDictionaryDao;
import com.lzlj.account.datadictionary.dto.CreateDataDictionaryDTO;
import com.lzlj.account.datadictionary.dto.DataDictionaryDTO;
import com.lzlj.account.datadictionary.dto.DataDictionaryQueryDTO;
import com.lzlj.account.datadictionary.dto.UpdateDataDictionaryDTO;
import com.lzlj.account.datadictionary.entity.DataDictionary;
import com.lzlj.account.datadictionary.service.DataDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据字典服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataDictionaryServiceImpl implements DataDictionaryService {

    private final DataDictionaryDao dataDictionaryDao;

    @Override
    public Long create(CreateDataDictionaryDTO dto) {
        // 检查字典编码唯一性
        if (checkCodeExists(dto.getDictCode(), null)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "字典编码已存在");
        }

        DataDictionary dict = new DataDictionary();
        BeanUtils.copyProperties(dto, dict);
        dataDictionaryDao.insert(dict);
        log.info("创建数据字典成功: id={}, dictCode={}", dict.getId(), dict.getDictCode());
        return dict.getId();
    }

    @Override
    public void update(Long id, UpdateDataDictionaryDTO dto) {
        DataDictionary existDict = dataDictionaryDao.selectById(id);
        if (existDict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查字典编码唯一性（排除自身）
        if (!dto.getDictCode().equals(existDict.getDictCode())) {
            if (checkCodeExists(dto.getDictCode(), id)) {
                throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "字典编码已存在");
            }
        }

        BeanUtils.copyProperties(dto, existDict);
        dataDictionaryDao.updateById(existDict);
        log.info("更新数据字典成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        DataDictionary dict = dataDictionaryDao.selectById(id);
        if (dict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        dataDictionaryDao.deleteById(id);
        log.info("删除数据字典成功: id={}", id);
    }

    @Override
    public DataDictionaryDTO getById(Long id) {
        DataDictionary dict = dataDictionaryDao.selectById(id);
        if (dict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(dict);
    }

    @Override
    public PageResult<DataDictionaryDTO> page(DataDictionaryQueryDTO query, Integer pageNum, Integer pageSize) {
        Page<DataDictionary> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(query.getDictType()), DataDictionary::getDictType, query.getDictType())
               .eq(query.getStatus() != null, DataDictionary::getStatus, query.getStatus())
               .orderByAsc(DataDictionary::getSort)
               .orderByDesc(DataDictionary::getCreateTime);

        IPage<DataDictionary> resultPage = dataDictionaryDao.selectPage(page, wrapper);

        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public List<DataDictionaryDTO> list() {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(DataDictionary::getDictType, DataDictionary::getSort);
        List<DataDictionary> dicts = dataDictionaryDao.selectList(wrapper);
        return dicts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<DataDictionaryDTO> getByType(String type) {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDictionary::getDictType, type)
               .eq(DataDictionary::getStatus, 1)
               .orderByAsc(DataDictionary::getSort)
               .orderByDesc(DataDictionary::getCreateTime);
        List<DataDictionary> dicts = dataDictionaryDao.selectList(wrapper);
        return dicts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<DataDictionaryDTO>> getAllGroup() {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDictionary::getStatus, 1)
               .orderByAsc(DataDictionary::getDictType, DataDictionary::getSort);
        List<DataDictionary> dicts = dataDictionaryDao.selectList(wrapper);

        return dicts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.groupingBy(DataDictionaryDTO::getDictType));
    }

    private boolean checkCodeExists(String dictCode, Long excludeId) {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDictionary::getDictCode, dictCode);
        if (excludeId != null) {
            wrapper.ne(DataDictionary::getId, excludeId);
        }
        return dataDictionaryDao.selectCount(wrapper) > 0;
    }

    private DataDictionaryDTO convertToDTO(DataDictionary dict) {
        DataDictionaryDTO dto = new DataDictionaryDTO();
        BeanUtils.copyProperties(dict, dto);
        return dto;
    }
}
