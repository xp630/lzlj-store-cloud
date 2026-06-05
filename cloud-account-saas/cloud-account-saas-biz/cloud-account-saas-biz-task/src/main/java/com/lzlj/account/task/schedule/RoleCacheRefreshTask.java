package com.lzlj.account.task.schedule;

import com.lzlj.account.common.api.feign.cache.SaasCacheFeignClient;
import com.lzlj.account.common.schedule.ScheduleTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 角色缓存刷新任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoleCacheRefreshTask implements ScheduleTask {

    private final SaasCacheFeignClient cacheFeignClient;

    @Override
    public String name() {
        return "role-refresh";
    }

    @Override
    public String execute() {
        log.info("开始执行角色缓存刷新任务...");
        try {
            cacheFeignClient.refreshRoles();
            String result = "角色缓存刷新成功";
            log.info("角色缓存刷新完成: {}", result);
            return result;
        } catch (Exception e) {
            log.error("角色缓存刷新失败", e);
            return "角色缓存刷新失败: " + e.getMessage();
        }
    }
}
