package com.lzlj.account.enums.controller;

import com.lzlj.account.common.core.enums.ModuleEnum;
import com.lzlj.account.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 枚举管理控制器
 */
@Tag(name = "枚举管理")
@RestController
@RequestMapping("/enums")
@RequiredArgsConstructor
public class SaasEnumController {

    @Operation(summary = "获取模块枚举")
    @GetMapping("/modules")
    public Result<Map<String, String>> getModules() {
        Map<String, String> modules = new HashMap<>();
        for (ModuleEnum module : ModuleEnum.values()) {
            modules.put(module.name(), module.getDescription());
        }
        return Result.success(modules);
    }

    @Operation(summary = "获取所有枚举")
    @GetMapping("/all")
    public Result<Map<String, Object>> getAllEnums() {
        Map<String, Object> allEnums = new HashMap<>();

        // 模块枚举
        Map<String, String> modules = new HashMap<>();
        for (ModuleEnum module : ModuleEnum.values()) {
            modules.put(module.name(), module.getDescription());
        }
        allEnums.put("modules", modules);

        return Result.success(allEnums);
    }
}
