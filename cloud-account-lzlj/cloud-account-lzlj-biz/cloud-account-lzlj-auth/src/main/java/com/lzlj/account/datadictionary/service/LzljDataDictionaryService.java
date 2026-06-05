package com.lzlj.account.datadictionary.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.domain.datadictionary.DataDictionaryDTO;
import com.lzlj.account.common.core.domain.datadictionary.DataDictionaryQueryDTO;
import com.lzlj.account.common.core.domain.datadictionary.SaveDataDictionaryDTO;
import com.lzlj.account.datadictionary.dto.CreateLzljDataDictionaryDTO;
import com.lzlj.account.datadictionary.dto.UpdateLzljDataDictionaryDTO;

import java.util.List;
import java.util.Map;

public interface LzljDataDictionaryService {
    Long create(CreateLzljDataDictionaryDTO dto);
    void update(Long id, UpdateLzljDataDictionaryDTO dto);
    void delete(Long id);
    DataDictionaryDTO getById(Long id);
    PageResult<DataDictionaryDTO> page(DataDictionaryQueryDTO query, Integer pageNum, Integer pageSize);
    List<DataDictionaryDTO> list();
    List<DataDictionaryDTO> getByType(String type);
    Map<String, List<DataDictionaryDTO>> getAllGroup();
    PageResult<DataDictionaryDTO> getTypesPage(DataDictionaryQueryDTO query, Integer pageNum, Integer pageSize);

    /**
     * 批量保存（创建/更新）
     */
    void saveBatch(List<SaveDataDictionaryDTO> dtos);
}
