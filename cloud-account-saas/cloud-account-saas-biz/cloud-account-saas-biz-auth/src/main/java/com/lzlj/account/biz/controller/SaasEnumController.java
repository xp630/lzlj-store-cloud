package com.lzlj.account.biz.controller;

import com.lzlj.account.common.core.enums.*;
import com.lzlj.account.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 枚举管理控制器
 */
@Tag(name = "枚举管理")
@RestController
@RequestMapping("/enums")
@RequiredArgsConstructor
public class SaasEnumController {

    @Operation(summary = "获取所有枚举")
    @GetMapping("/all")
    public Result<Map<String, Object>> getAllEnums() {
        Map<String, Object> allEnums = new LinkedHashMap<>();
        allEnums.put("PaymentMethod", getEnumValues("PaymentMethod"));
        allEnums.put("BusinessType", getEnumValues("BusinessType"));
        allEnums.put("Module", getEnumValues("Module"));
        allEnums.put("ParamType", getEnumValues("ParamType"));
        allEnums.put("PaymentChannel", getEnumValues("PaymentChannel"));
        return Result.success(allEnums);
    }

    private List<Map<String, String>> getEnumValues(String enumName) {
        List<Map<String, String>> list = new ArrayList<>();

        switch (enumName) {
            case "PaymentMethod":
                for (PaymentMethodEnum e : PaymentMethodEnum.values()) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("code", e.getCode());
                    map.put("name", e.getName());
                    list.add(map);
                }
                break;
            case "BusinessType":
                for (BusinessTypeEnum e : BusinessTypeEnum.values()) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("code", e.name());
                    map.put("name", e.getDescription());
                    list.add(map);
                }
                break;
            case "Module":
                for (ModuleEnum e : ModuleEnum.values()) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("code", e.name());
                    map.put("name", e.getDescription());
                    list.add(map);
                }
                break;
            case "ParamType":
                for (ParamTypeEnum e : ParamTypeEnum.values()) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("code", e.getCode());
                    map.put("name", e.getName());
                    list.add(map);
                }
                break;
            case "PaymentChannel":
                for (PaymentChannelEnum e : PaymentChannelEnum.values()) {
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("code", e.getCode());
                    map.put("name", e.getName());
                    list.add(map);
                }
                break;
            default:
                return Collections.emptyList();
        }
        return list;
    }
}
