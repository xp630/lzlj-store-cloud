package com.lzlj.account.common.api.feign.fallback;

import com.lzlj.account.common.api.feign.UserFeignClient;
import com.lzlj.account.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户Feign客户端降级处理
 */
@Slf4j
@Component
public class    UserFeignClientFallback implements UserFeignClient {

    @Override
    public Result<UserFeignClient.UserInfo> getById(Long id) {
        log.warn("Feign调用用户服务失败: getById({})", id);
        return Result.fail("用户服务暂时不可用");
    }

    @Override
    public Result<UserInfo> getCurrentUser() {
        log.warn("Feign调用用户服务失败: getCurrentUser()");
        return Result.fail("用户服务暂时不可用");
    }

}
