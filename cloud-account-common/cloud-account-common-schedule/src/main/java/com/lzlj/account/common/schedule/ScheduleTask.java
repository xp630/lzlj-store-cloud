package com.lzlj.account.common.schedule;

/**
 * 调度任务接口
 * 抽象调度框架，业务只需实现此接口即可接入调度系统
 */
public interface ScheduleTask {

    /**
     * 任务名称
     * 用于在调度系统中唯一标识任务
     */
    String name();

    /**
     * 执行任务
     * @return 执行结果描述
     */
    String execute();
}
