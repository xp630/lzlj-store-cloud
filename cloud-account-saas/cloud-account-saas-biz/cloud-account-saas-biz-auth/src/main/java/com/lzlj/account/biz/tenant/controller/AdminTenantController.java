package com.lzlj.account.biz.tenant.controller;

import com.lzlj.account.biz.tenant.dto.AdminTenantDTO;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.biz.tenant.dto.AssignTenantDTO;
import com.lzlj.account.biz.tenant.service.AdminTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理员租户管理控制器
 */
@Tag(name = "管理员租户管理")
@RestController
@RequestMapping("/admin/tenant")
@RequiredArgsConstructor
public class AdminTenantController {

    private final AdminTenantService adminTenantService;

    @Operation(summary = "获取管理员可管理的租户列表")
    @GetMapping("/list")
    public Result<List<AdminTenantDTO>> getAdminTenants(
            @RequestParam Long adminUserId) {
        return Result.success(adminTenantService.getAdminTenants(adminUserId));
    }

    @Operation(summary = "分配管理员可管理的租户")
    @PutMapping("/{adminUserId}/tenants")
    public Result<Void> assignTenants(
            @PathVariable Long adminUserId,
            @Valid @RequestBody AssignTenantDTO dto) {
        adminTenantService.assignTenants(adminUserId, dto);
        return Result.success();
    }
}
