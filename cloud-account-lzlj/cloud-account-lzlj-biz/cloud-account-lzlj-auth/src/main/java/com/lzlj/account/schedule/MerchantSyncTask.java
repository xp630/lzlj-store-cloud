package com.lzlj.account.schedule;

import com.lzlj.account.common.schedule.ScheduleTask;
import com.lzlj.account.merchant.service.LzljMerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 商户同步任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MerchantSyncTask implements ScheduleTask {

    private final LzljMerchantService merchantService;

    @Override
    public String name() {
        return "merchant-sync";
    }

    @Override
    public String execute() {
        log.info("开始执行商户同步任务...");
        try {
            int count = merchantService.syncAllFromSaas(null);
            String result = "商户同步成功: " + count + " 条";
            log.info("商户同步完成，共 {} 条", count);
            return result;
        } catch (Exception e) {
            log.error("商户同步失败", e);
            return "商户同步失败: " + e.getMessage();
        }
    }
}
