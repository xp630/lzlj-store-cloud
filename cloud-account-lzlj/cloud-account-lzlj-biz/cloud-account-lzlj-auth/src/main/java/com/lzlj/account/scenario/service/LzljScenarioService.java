package com.lzlj.account.scenario.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.scenario.dto.CreateScenarioDTO;
import com.lzlj.account.scenario.dto.ScenarioDTO;
import com.lzlj.account.scenario.dto.ScenarioQueryDTO;
import com.lzlj.account.scenario.dto.UpdateScenarioDTO;

import java.util.List;

/**
 * LZLJ 业务场景服务接口
 */
public interface LzljScenarioService {

    /**
     * 创建业务场景
     */
    ScenarioDTO create(CreateScenarioDTO dto);

    /**
     * 更新业务场景
     */
    void update(Long id, UpdateScenarioDTO dto);

    /**
     * 删除业务场景
     */
    void delete(Long id);

    /**
     * 获取业务场景详情
     */
    ScenarioDTO getById(Long id);

    /**
     * 业务场景分页列表
     */
    PageResult<ScenarioDTO> page(ScenarioQueryDTO query);

    /**
     * 获取所有启用的业务场景
     */
    List<ScenarioDTO> listEnabled();

    /**
     * 获取业务场景关联的支付通道ID列表
     */
    List<Long> getChannelIds(Long scenarioId);
}
