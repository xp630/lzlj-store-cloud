package com.lzlj.account.gateway.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * API访问日志记录器
 * 使用异步队列将日志写入本地文件，按天分片
 */
@Slf4j
@Component
public class ApiAccessLogger {

    // 注意：仅支持绝对路径，不支持相对路径（相对路径以进程启动目录为基准）
    @Value("${openapi.access-log.path:/tmp/gateway-api-access.log}")
    private String logFilePath;

    private static final int QUEUE_SIZE = 10000;
    private static final int BATCH_SIZE = 100;
    private static final long FLUSH_INTERVAL_MS = 1000;

    private final BlockingQueue<ApiAccessLog> queue = new LinkedBlockingQueue<>(QUEUE_SIZE);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private volatile boolean running = true;
    private Thread flushThread;
    private String currentDate = "";

    @PostConstruct
    public void init() {
        currentDate = getToday();
        flushThread = new Thread(this::flushLoop, "api-access-log-flusher");
        flushThread.setDaemon(true);
        flushThread.start();
        log.info("API访问日志记录器已启动，日志路径: {}, 日期: {}", getLogFile(), currentDate);
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        if (flushThread != null) {
            flushThread.interrupt();
        }
        flushAll();
        log.info("API访问日志记录器已关闭");
    }

    /**
     * 获取当前日志文件路径（按天分片）
     */
    private String getLogFile() {
        String today = getToday();
        // 如果日期变了，说明跨天了，生成新的日志文件
        if (!today.equals(currentDate)) {
            currentDate = today;
        }
        return logFilePath + "." + currentDate + ".log";
    }

    private String getToday() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    /**
     * 异步记录API访问日志
     */
    public void logAsync(ApiAccessLog accessLog) {
        if (!queue.offer(accessLog)) {
            log.warn("API访问日志队列已满，跳过日志: {}", accessLog.getPath());
        }
    }

    /**
     * 记录日志的便捷方法
     */
    public void log(Long apiKeyId, String apiKey, Long tenantId, String method, String path,
                    String requestBody, String responseBody, Integer statusCode,
                    Long duration, String ip, String userAgent, String errorMsg) {
        ApiAccessLog accessLog = new ApiAccessLog();
        accessLog.setApiKeyId(apiKeyId);
        accessLog.setApiKey(apiKey);
        accessLog.setTenantId(tenantId);
        accessLog.setMethod(method);
        accessLog.setPath(path);
        accessLog.setRequestBody(requestBody);
        accessLog.setResponseBody(responseBody);
        accessLog.setStatusCode(statusCode);
        accessLog.setDuration(duration);
        accessLog.setIp(ip);
        accessLog.setUserAgent(userAgent);
        accessLog.setErrorMsg(errorMsg);
        logAsync(accessLog);
    }

    /**
     * 刷新循环
     */
    private void flushLoop() {
        while (running) {
            try {
                Thread.sleep(FLUSH_INTERVAL_MS);
                flush();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 批量刷新日志到文件
     */
    private void flush() {
        String logFile = getLogFile();
        int count = 0;
        try {
            // 确保目录存在
            File file = new File(logFile);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                while (count < BATCH_SIZE) {
                    ApiAccessLog accessLog = queue.poll();
                    if (accessLog == null) {
                        break;
                    }
                    writer.println(objectMapper.writeValueAsString(accessLog));
                    count++;
                }
                if (count > 0) {
                    writer.flush();
                    log.info("已刷新 {} 条API访问日志到文件: {}", count, logFile);
                }
            }
        } catch (IOException e) {
            log.error("写入API访问日志失败: {}", logFile, e);
        }
    }

    /**
     * 关闭前将所有日志刷新到文件
     */
    private void flushAll() {
        String logFile = getLogFile();
        int total = 0;
        try {
            // 确保目录存在
            File file = new File(logFile);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                ApiAccessLog accessLog;
                while ((accessLog = queue.poll()) != null) {
                    writer.println(objectMapper.writeValueAsString(accessLog));
                    total++;
                }
                writer.flush();
                log.info("已刷新剩余 {} 条API访问日志到文件: {}", total, logFile);
            }
        } catch (IOException e) {
            log.error("写入API访问日志失败: {}", logFile, e);
        }
    }
}
