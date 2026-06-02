package com.lzlj.account.common.schedule.adapter.xxljob;

import com.lzlj.account.common.schedule.ScheduleTask;
import com.lzlj.account.common.schedule.ScheduleTaskRegistry;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * XXL-JOB 适配器
 * 将 XXL-JOB 与 ScheduleTask 接口对接
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class XxlJobScheduleAdapter {

    private final ScheduleTaskRegistry registry;

    /**
     * XXL-JOB 入口方法
     * 通过 JobHandler 名称 "scheduleTaskExecutor" 触发
     * 任务名通过 XXL-JOB 的 JobParam 传递
     */
    @XxlJob("scheduleTaskExecutor")
    public String execute() {
        // 获取任务名（由调度中心传递）
        String taskName = getTaskName();

        if (taskName == null || taskName.isEmpty()) {
            String msg = "任务名为空，请检查调度参数";
            XxlJobLogger.log(msg);
            log.warn(msg);
            return msg;
        }

        log.info("XXL-JOB 收到任务执行请求: taskName={}", taskName);
        XxlJobLogger.log("开始执行任务: " + taskName);

        try {
            ScheduleTask task = registry.getTask(taskName);
            if (task == null) {
                String msg = "Task not found: " + taskName;
                XxlJobLogger.log(msg);
                log.error("XXL-JOB: {}", msg);
                return msg;
            }

            String result = task.execute();
            XxlJobLogger.log("任务执行完成: " + taskName + ", result: " + result);
            log.info("XXL-JOB 任务执行完成: taskName={}, result={}", taskName, result);
            return result;

        } catch (Exception e) {
            String msg = "任务执行异常: " + taskName + ", error: " + e.getMessage();
            XxlJobLogger.log(msg);
            log.error("XXL-JOB 任务执行异常: taskName={}", taskName, e);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * 获取任务名
     * 优先从 JobParameter 获取，否则从 JobHandler 名称中解析
     */
    private String getTaskName() {
        // 从 XXL-JOB 上下文获取任务参数
        String taskName = com.xxl.job.core.util.XxlJobHelper.getJobParam();
        return taskName;
    }
}
