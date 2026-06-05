package com.lzlj.account.biz.merchantchannel.controller;

import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.biz.merchantchannel.dto.CreateMerchantChannelDTO;
import com.lzlj.account.biz.merchantchannel.dto.MerchantChannelDTO;
import com.lzlj.account.biz.merchantchannel.dto.UpdateMerchantChannelDTO;
import com.lzlj.account.biz.merchantchannel.service.SaasMerchantChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 内部商户渠道管理接口
 * <p>
 * 供内部服务调用，不对外暴露
 */
@Tag(name = "内部接口-商户渠道管理")
@RestController
@RequestMapping("/internal/merchant-channels")
@RequiredArgsConstructor
public class InternalMerchantChannelController {

    private final SaasMerchantChannelService merchantChannelService;

    @Operation(summary = "开通商户渠道")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody CreateMerchantChannelDTO dto) {
        return Result.success(merchantChannelService.create(dto));
    }

    @Operation(summary = "更新商户渠道")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UpdateMerchantChannelDTO dto) {
        merchantChannelService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "关闭商户渠道")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        merchantChannelService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取商户渠道详情")
    @GetMapping("/{id}")
    public Result<MerchantChannelDTO> getById(@PathVariable Long id) {
        return Result.success(merchantChannelService.getById(id));
    }

    @Operation(summary = "获取商户所有渠道列表")
    @GetMapping("/merchant/{merchantId}")
    public Result<List<MerchantChannelDTO>> listByMerchantId(@PathVariable Long merchantId) {
        return Result.success(merchantChannelService.listByMerchantId(merchantId));
    }
}
