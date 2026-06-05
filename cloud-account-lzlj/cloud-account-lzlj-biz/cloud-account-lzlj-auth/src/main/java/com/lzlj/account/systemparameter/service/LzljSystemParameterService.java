package com.lzlj.account.systemparameter.service;

import com.lzlj.account.common.core.domain.PageRequest;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.systemparameter.dto.*;

import java.util.List;

public interface LzljSystemParameterService {
    Long create(CreateLzljSystemParameterDTO dto);
    void update(Long id, UpdateLzljSystemParameterDTO dto);
    void delete(Long id);
    LzljSystemParameterDTO getById(Long id);
    LzljSystemParameterDTO getByKey(String key);
    PageResult<LzljSystemParameterDTO> page(PageRequest<LzljSystemParameterQueryDTO> pageRequest);
    List<LzljSystemParameterDTO> list();
}
