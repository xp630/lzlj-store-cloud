package com.lzlj.account.scenario.controller;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.scenario.dto.CreateScenarioDTO;
import com.lzlj.account.scenario.dto.ScenarioDTO;
import com.lzlj.account.scenario.dto.ScenarioQueryDTO;
import com.lzlj.account.scenario.dto.UpdateScenarioDTO;
import com.lzlj.account.scenario.service.LzljScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * LZLJ 业务场景控制器
 */
@Tag(name = "业务场景管理")
@RestController
@RequestMapping("/scenario")
@RequiredArgsConstructor
public class LzljScenarioController {

    private final LzljScenarioService scenarioService;

    @Operation(summary = "创建业务场景")
    @PostMapping
    public Result<ScenarioDTO> create(@RequestBody @Valid CreateScenarioDTO dto) {
        return Result.success(scenarioService.create(dto));
    }

    @Operation(summary = "更新业务场景")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UpdateScenarioDTO dto) {
        scenarioService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除业务场景")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        scenarioService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取业务场景详情")
    @GetMapping("/{id}")
    public Result<ScenarioDTO> getById(@PathVariable Long id) {
        return Result.success(scenarioService.getById(id));
    }

    @Operation(summary = "业务场景分页列表")
    @GetMapping("/page")
    public Result<PageResult<ScenarioDTO>> page(ScenarioQueryDTO query) {
        return Result.success(scenarioService.page(query));
    }

    @Operation(summary = "获取所有启用的业务场景")
    @GetMapping("/enabled")
    public Result<List<ScenarioDTO>> listEnabled() {
        return Result.success(scenarioService.listEnabled());
    }

    @Operation(summary = "获取母户的业务场景列表")
    @GetMapping("/merchant/{merchantId}")
    public Result<List<ScenarioDTO>> listByMerchant(@PathVariable Long merchantId) {
        ScenarioQueryDTO query = new ScenarioQueryDTO();
        query.setMerchantId(merchantId);
        query.setStatus(1);
        return Result.success(scenarioService.page(query).getRecords());
    }

    @Operation(summary = "获取业务场景关联的支付通道")
    @GetMapping("/{id}/channels")
    public Result<List<Long>> getChannelIds(@PathVariable Long id) {
        return Result.success(scenarioService.getChannelIds(id));
    }
}
