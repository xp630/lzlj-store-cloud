package com.lzlj.account.schedule;

import com.lzlj.account.common.schedule.ScheduleTask;
import com.lzlj.account.paymentchannel.service.LzljPaymentChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 支付通道同步任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentChannelSyncTask implements ScheduleTask {

    private final LzljPaymentChannelService paymentChannelService;

    @Override
    public String name() {
        return "payment-channel-sync";
    }

    @Override
    public String execute() {
        log.info("开始执行支付通道同步任务...");
        try {
            int count = paymentChannelService.syncFromSaas();
            String result = "支付通道同步成功: " + count + " 条";
            log.info("支付通道同步完成，共 {} 条", count);
            return result;
        } catch (Exception e) {
            log.error("支付通道同步失败", e);
            return "支付通道同步失败: " + e.getMessage();
        }
    }
}
