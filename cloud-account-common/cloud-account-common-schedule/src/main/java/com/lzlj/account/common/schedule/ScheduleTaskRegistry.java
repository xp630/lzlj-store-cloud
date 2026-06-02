package com.lzlj.account.common.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 调度任务注册器
 * 自动扫描并注册所有 ScheduleTask 实现
 */
@Slf4j
@Component
public class ScheduleTaskRegistry {

    private final List<ScheduleTask> tasks;
    private final Map<String, ScheduleTask> taskMap = new ConcurrentHashMap<>();

    public ScheduleTaskRegistry(List<ScheduleTask> tasks) {
        this.tasks = tasks;
    }

    @PostConstruct
    public void register() {
        if (tasks == null || tasks.isEmpty()) {
            log.warn("未发现任何 ScheduleTask 实现");
            return;
        }

        for (ScheduleTask task : tasks) {
            String taskName = task.name();
            if (!taskMap.containsKey(taskName)) {
                taskMap.put(taskName, task);
                log.info("注册调度任务: {}", taskName);
            } else {
                log.warn("重复的任务名: {}", taskName);
            }
        }

        log.info("共注册 {} 个调度任务: {}", taskMap.size(), taskMap.keySet());
    }

    /**
     * 根据任务名查找任务
     */
    public ScheduleTask getTask(String name) {
        return taskMap.get(name);
    }

    /**
     * 获取所有已注册的任务名
     */
    public List<String> getTaskNames() {
        return taskMap.keySet().stream().collect(Collectors.toList());
    }
}
