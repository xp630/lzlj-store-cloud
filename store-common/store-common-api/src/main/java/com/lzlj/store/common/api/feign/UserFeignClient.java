package com.lzlj.store.common.api.feign;

import com.lzlj.store.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务 Feign 客户端
 */
@FeignClient(
    name = "store-user",
    path = "/user"
)
public interface UserFeignClient {

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    Result<UserInfo> getById(@PathVariable("id") Long id);

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    Result<UserInfo> getUserInfo(@RequestParam("userId") Long userId);

    /**
     * 获取多个用户信息
     */
    @GetMapping("/batch")
    Result<java.util.List<UserInfo>> getBatchUsers(@RequestParam("userIds") String userIds);

    /**
     * 验证用户状态
     */
    @GetMapping("/valid/{userId}")
    Result<Boolean> validateUserStatus(@PathVariable("userId") Long userId);

    /**
     * 用户信息DTO
     */
    record UserInfo(
        Long id,
        String username,
        String realName,
        String phone,
        String email,
        Long tenantId,
        Long orgId
    ) {}
}
