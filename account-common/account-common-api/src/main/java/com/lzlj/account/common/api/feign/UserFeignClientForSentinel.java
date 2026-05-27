package com.lzlj.account.common.api.feign;

import com.lzlj.account.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 客户端（无 Fallback，仅用于 Sentinel 测试）
 * 注意：这个客户端不配置 fallback，Sentinel 可以拦截到异常
 */
@FeignClient(
        name = "saas-auth-no-fallback",
        path = "/user"
)
public interface UserFeignClientForSentinel {

    @GetMapping("/{id}")
    Result<UserFeignClient.UserInfo> getById(@PathVariable("id") Long id);

    @GetMapping("/current")
    Result<UserFeignClient.UserInfo> getCurrentUser();
}
