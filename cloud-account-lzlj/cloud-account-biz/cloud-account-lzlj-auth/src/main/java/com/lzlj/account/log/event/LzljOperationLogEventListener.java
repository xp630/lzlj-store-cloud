package com.lzlj.account.log.event;

import com.lzlj.account.log.service.LzljLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * LZLJ 操作日志事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LzljOperationLogEventListener {

    private final LzljLogService logService;

    @Async
    @EventListener
    public void handleOperationLogEvent(LzljOperationLogEvent event) {
        try {
            logService.logOperation(
                    event.getUserId(),
                    event.getOrgId(),
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
