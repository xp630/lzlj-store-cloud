package com.lzlj.account.log.event;

import com.lzlj.account.log.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 操作日志事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OperationLogEventListener {

    private final LogService logService;

    @Async
    @EventListener
    public void handleOperationLogEvent(OperationLogEvent event) {
        try {
            logService.logOperation(
                    event.getUserId(),
                    event.getTenantId(),
                    event.getUsername(),
                    event.getModule(),
                    event.getOperation(),
                    event.getContent(),
                    event.getBizId(),
                    event.getIp(),
                    event.getUserAgent()
            );
        } catch (Exception e) {
            log.error("处理操作日志事件失败", e);
        }
    }
}
