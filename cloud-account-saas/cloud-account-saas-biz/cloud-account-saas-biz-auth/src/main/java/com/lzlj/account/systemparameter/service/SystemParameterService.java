package com.lzlj.account.systemparameter.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.systemparameter.dto.CreateSystemParameterDTO;
import com.lzlj.account.systemparameter.dto.SystemParameterDTO;
import com.lzlj.account.systemparameter.dto.SystemParameterQueryDTO;
import com.lzlj.account.systemparameter.dto.UpdateSystemParameterDTO;

import java.util.List;

/**
 * 系统参数服务接口
 */
public interface SystemParameterService {

    /**
     * 创建系统参数
     */
    Long create(CreateSystemParameterDTO dto);

    /**
     * 更新系统参数
     */
    void update(Long id, UpdateSystemParameterDTO dto);

    /**
     * 删除系统参数
     */
    void delete(Long id);

    /**
     * 获取系统参数详情
     */
    SystemParameterDTO getById(Long id);

    /**
     * 根据key获取系统参数
     */
    SystemParameterDTO getByKey(String key);

    /**
     * 分页查询系统参数
     */
    PageResult<SystemParameterDTO> page(SystemParameterQueryDTO query, Integer pageNum, Integer pageSize);

    /**
     * 获取系统参数列表
     */
    List<SystemParameterDTO> list();
}
