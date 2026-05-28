package com.lzlj.account.log.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * LZLJ 操作日志事件
 */
@Data
@AllArgsConstructor
public class LzljOperationLogEvent {
    private Long userId;
    private Long orgId;
    private String username;
    private String module;
    private String operation;
    private String content;
    private Long bizId;
    private String ip;
    private String userAgent;
}
