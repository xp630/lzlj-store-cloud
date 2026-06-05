package com.lzlj.account.datadictionary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.domain.datadictionary.DataDictionaryDTO;
import com.lzlj.account.common.core.domain.datadictionary.DataDictionaryQueryDTO;
import com.lzlj.account.common.core.domain.datadictionary.SaveDataDictionaryDTO;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.datadictionary.dao.LzljDataDictionaryDao;
import com.lzlj.account.datadictionary.dto.CreateLzljDataDictionaryDTO;
import com.lzlj.account.datadictionary.dto.UpdateLzljDataDictionaryDTO;
import com.lzlj.account.datadictionary.entity.LzljDataDictionary;
import com.lzlj.account.datadictionary.service.LzljDataDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LzljDataDictionaryServiceImpl implements LzljDataDictionaryService {

    private final LzljDataDictionaryDao lzljDataDictionaryDao;

    @Override
    public Long create(CreateLzljDataDictionaryDTO dto) {
        if (checkCodeExists(dto.getDictCode(), null)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "字典编码已存在");
        }
        LzljDataDictionary dict = new LzljDataDictionary();
        BeanUtils.copyProperties(dto, dict);
        lzljDataDictionaryDao.insert(dict);
        log.info("创建LZLJ数据字典成功: id={}, dictCode={}", dict.getId(), dict.getDictCode());
        return dict.getId();
    }

    @Override
    public void update(Long id, UpdateLzljDataDictionaryDTO dto) {
        LzljDataDictionary existDict = lzljDataDictionaryDao.selectById(id);
        if (existDict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (!dto.getDictCode().equals(existDict.getDictCode())) {
            if (checkCodeExists(dto.getDictCode(), id)) {
                throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "字典编码已存在");
            }
        }
        BeanUtils.copyProperties(dto, existDict);
        lzljDataDictionaryDao.updateById(existDict);
        log.info("更新LZLJ数据字典成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        LzljDataDictionary dict = lzljDataDictionaryDao.selectById(id);
        if (dict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        lzljDataDictionaryDao.deleteById(id);
        log.info("删除LZLJ数据字典成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<SaveDataDictionaryDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return;
        }
        // 基于第一个 dictCode 删除该类型的所有记录
        String dictType = dtos.get(0).getDictType();
        LambdaQueryWrapper<LzljDataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljDataDictionary::getDictType, dictType);
        lzljDataDictionaryDao.delete(wrapper);

        // 批量新增
        for (SaveDataDictionaryDTO dto : dtos) {
            LzljDataDictionary dict = new LzljDataDictionary();
            BeanUtils.copyProperties(dto, dict);
            lzljDataDictionaryDao.insert(dict);
        }
        log.info("批量保存LZLJ数据字典成功: dictType={}, {}条", dictType, dtos.size());
    }

    @Override
    public DataDictionaryDTO getById(Long id) {
        LzljDataDictionary dict = lzljDataDictionaryDao.selectById(id);
        if (dict == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(dict);
    }

    @Override
    public PageResult<DataDictionaryDTO> page(DataDictionaryQueryDTO query, Integer pageNum, Integer pageSize) {
        Page<LzljDataDictionary> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LzljDataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(query.getDictType()), LzljDataDictionary::getDictType, query.getDictType())
               .eq(query.getStatus() != null, LzljDataDictionary::getStatus, query.getStatus())
               .orderByAsc(LzljDataDictionary::getSort)
               .orderByDesc(LzljDataDictionary::getCreateTime);
        IPage<LzljDataDictionary> resultPage = lzljDataDictionaryDao.selectPage(page, wrapper);
        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public List<DataDictionaryDTO> list() {
        LambdaQueryWrapper<LzljDataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(LzljDataDictionary::getDictType, LzljDataDictionary::getSort);
        List<LzljDataDictionary> dicts = lzljDataDictionaryDao.selectList(wrapper);
        return dicts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<DataDictionaryDTO> getByType(String type) {
        LambdaQueryWrapper<LzljDataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljDataDictionary::getDictType, type)
               .eq(LzljDataDictionary::getStatus, 1)
               .orderByAsc(LzljDataDictionary::getSort)
               .orderByDesc(LzljDataDictionary::getCreateTime);
        List<LzljDataDictionary> dicts = lzljDataDictionaryDao.selectList(wrapper);
        return dicts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<DataDictionaryDTO>> getAllGroup() {
        LambdaQueryWrapper<LzljDataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljDataDictionary::getStatus, 1)
               .orderByAsc(LzljDataDictionary::getDictType, LzljDataDictionary::getSort);
        List<LzljDataDictionary> dicts = lzljDataDictionaryDao.selectList(wrapper);
        return dicts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.groupingBy(DataDictionaryDTO::getDictType));
    }

    @Override
    public PageResult<DataDictionaryDTO> getTypesPage(DataDictionaryQueryDTO query, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<LzljDataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(query.getDictType()), LzljDataDictionary::getDictType, query.getDictType())
               .eq(query.getStatus() != null, LzljDataDictionary::getStatus, query.getStatus())
               .orderByAsc(LzljDataDictionary::getDictType);
        List<LzljDataDictionary> dicts = lzljDataDictionaryDao.selectList(wrapper);

        // 按 dictType 分组，每组取第一条
        List<DataDictionaryDTO> types = dicts.stream()
                .collect(Collectors.groupingBy(LzljDataDictionary::getDictType))
                .values().stream()
                .map(group -> group.get(0))
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 分页
        int total = types.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<DataDictionaryDTO> pageList = start < total ? types.subList(start, end) : new ArrayList<>();

        return new PageResult<>(pageList, (long) total, (long) pageNum, (long) pageSize);
    }

    private boolean checkCodeExists(String dictCode, Long excludeId) {
        LambdaQueryWrapper<LzljDataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljDataDictionary::getDictCode, dictCode);
        if (excludeId != null) {
            wrapper.ne(LzljDataDictionary::getId, excludeId);
        }
        return lzljDataDictionaryDao.selectCount(wrapper) > 0;
    }

    private DataDictionaryDTO convertToDTO(LzljDataDictionary dict) {
        DataDictionaryDTO dto = new DataDictionaryDTO();
        BeanUtils.copyProperties(dict, dto);
        return dto;
    }
}
