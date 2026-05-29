package com.lzlj.account.datadictionary.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.datadictionary.dto.*;

import java.util.List;
import java.util.Map;

public interface LzljDataDictionaryService {
    Long create(CreateLzljDataDictionaryDTO dto);
    void update(Long id, UpdateLzljDataDictionaryDTO dto);
    void delete(Long id);
    LzljDataDictionaryDTO getById(Long id);
    PageResult<LzljDataDictionaryDTO> page(LzljDataDictionaryQueryDTO query, Integer pageNum, Integer pageSize);
    List<LzljDataDictionaryDTO> list();
    List<LzljDataDictionaryDTO> getByType(String type);
    Map<String, List<LzljDataDictionaryDTO>> getAllGroup();
}
