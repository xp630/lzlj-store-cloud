package com.lzlj.account.datadictionary.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.domain.datadictionary.DataDictionaryDTO;
import com.lzlj.account.common.core.domain.datadictionary.DataDictionaryQueryDTO;
import com.lzlj.account.common.core.domain.datadictionary.SaveDataDictionaryDTO;
import com.lzlj.account.datadictionary.dto.CreateDataDictionaryDTO;
import com.lzlj.account.datadictionary.dto.UpdateDataDictionaryDTO;

import java.util.List;
import java.util.Map;

/**
 * 数据字典服务接口
 */
public interface SaasDataDictionaryService {

    /**
     * 创建数据字典
     */
    Long create(CreateDataDictionaryDTO dto);

    /**
     * 更新数据字典
     */
    void update(Long id, UpdateDataDictionaryDTO dto);

    /**
     * 删除数据字典
     */
    void delete(Long id);

    /**
     * 获取数据字典详情
     */
    DataDictionaryDTO getById(Long id);

    /**
     * 分页查询数据字典
     */
    PageResult<DataDictionaryDTO> page(DataDictionaryQueryDTO query, Integer pageNum, Integer pageSize);

    /**
     * 根据类型获取数据字典
     */
    List<DataDictionaryDTO> getByType(String type);

    /**
     * 获取所有字典类型分组
     */
    Map<String, List<DataDictionaryDTO>> getAllGroup();

    /**
     * 获取所有字典类型列表（去重）
     */
    PageResult<DataDictionaryDTO> getTypesPage(DataDictionaryQueryDTO query, Integer pageNum, Integer pageSize);

    /**
     * 批量保存（创建/更新）
     */
    void saveBatch(List<SaveDataDictionaryDTO> dtos);
}
