package com.lzlj.account.task.schedule;

import com.lzlj.account.common.api.feign.cache.SaasCacheFeignClient;
import com.lzlj.account.common.schedule.ScheduleTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 菜单缓存刷新任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuCacheRefreshTask implements ScheduleTask {

    private final SaasCacheFeignClient cacheFeignClient;

    @Override
    public String name() {
        return "menu-refresh";
    }

    @Override
    public String execute() {
        log.info("开始执行菜单缓存刷新任务...");
        try {
            cacheFeignClient.refreshMenus();
            String result = "菜单缓存刷新成功";
            log.info("菜单缓存刷新完成: {}", result);
            return result;
        } catch (Exception e) {
            log.error("菜单缓存刷新失败", e);
            return "菜单缓存刷新失败: " + e.getMessage();
        }
    }
}
