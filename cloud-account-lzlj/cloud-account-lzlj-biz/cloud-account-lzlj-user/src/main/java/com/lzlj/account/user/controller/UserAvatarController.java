package com.lzlj.account.user.controller;

import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.common.oss.dto.PresignedUrlRequest;
import com.lzlj.account.common.oss.dto.PresignedUrlResponse;
import com.lzlj.account.common.oss.facade.OssUploadFacade;
import com.lzlj.account.common.core.tenant.TenantContext;
import com.lzlj.account.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户头像控制器
 */
@Tag(name = "用户头像")
@RestController
@RequestMapping("/user/avatar")
@RequiredArgsConstructor
public class UserAvatarController {

    private final OssUploadFacade ossUploadFacade;
    private final UserService userService;

    @Operation(summary = "获取头像预签名上传URL")
    @PostMapping("/presigned-url")
    public Result<PresignedUrlResponse> getAvatarPresignedUrl(@Valid @RequestBody PresignedUrlRequest request) {
        // 设置类型为 avatar
        request.setType("avatar");
        Long userId = UserContext.getUserId() != null ? UserContext.getUserId() : 0L;
        Long tenantId = TenantContext.getTenantId() != null ? TenantContext.getTenantId() : 0L;
        return ossUploadFacade.generateUploadUrl(request, userId, tenantId);
    }

    @Operation(summary = "更新用户头像")
    @PutMapping
    public Result<Void> updateAvatar(@RequestParam String avatar) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        userService.updateAvatar(userId, avatar);
        return Result.success();
    }
}
