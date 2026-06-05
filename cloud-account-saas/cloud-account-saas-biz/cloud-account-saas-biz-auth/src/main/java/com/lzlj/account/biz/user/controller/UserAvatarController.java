package com.lzlj.account.biz.user.controller;

import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.common.core.result.Result;
import com.lzlj.account.biz.user.service.SaasUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户头像控制器
 */
@Tag(name = "用户头像")
@RestController
@RequestMapping("/user/avatar")
@RequiredArgsConstructor
public class UserAvatarController {

    private final SaasUserService userService;

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
