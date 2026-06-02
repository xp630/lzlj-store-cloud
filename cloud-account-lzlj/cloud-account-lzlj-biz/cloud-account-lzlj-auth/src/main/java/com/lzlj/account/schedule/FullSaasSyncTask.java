package com.lzlj.account.schedule;

import com.lzlj.account.common.schedule.ScheduleTask;
import com.lzlj.account.merchant.service.LzljMerchantService;
import com.lzlj.account.paymentchannel.service.LzljPaymentChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 全量同步任务（支付通道 + 商户）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FullSaasSyncTask implements ScheduleTask {

    private final LzljPaymentChannelService paymentChannelService;
    private final LzljMerchantService merchantService;

    @Override
    public String name() {
        return "full-saas-sync";
    }

    @Override
    public String execute() {
        log.info("开始执行全量同步任务...");
        StringBuilder result = new StringBuilder();

        // 1. 同步支付通道
        try {
            int channelCount = paymentChannelService.syncFromSaas();
            result.append("支付通道: ").append(channelCount).append(" 条; ");
            log.info("支付通道同步完成，共 {} 条", channelCount);
        } catch (Exception e) {
            result.append("支付通道同步失败: ").append(e.getMessage()).append("; ");
            log.error("支付通道同步失败", e);
        }

        // 2. 同步商户
        try {
            int merchantCount = merchantService.syncAllFromSaas(null);
            result.append("商户: ").append(merchantCount).append(" 条");
            log.info("商户同步完成，共 {} 条", merchantCount);
        } catch (Exception e) {
            result.append("商户同步失败: ").append(e.getMessage());
            log.error("商户同步失败", e);
        }

        String finalResult = "全量同步完成. " + result;
        log.info("全量同步完成. 结果: {}", result);
        return finalResult;
    }
}
