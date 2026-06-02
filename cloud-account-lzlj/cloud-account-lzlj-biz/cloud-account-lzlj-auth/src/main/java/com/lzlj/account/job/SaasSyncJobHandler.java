package com.lzlj.account.job;

import com.lzlj.account.merchant.service.LzljMerchantService;
import com.lzlj.account.paymentchannel.service.LzljPaymentChannelService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SaaS 数据同步 XXL-JOB 处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaasSyncJobHandler {

    private final LzljPaymentChannelService paymentChannelService;
    private final LzljMerchantService merchantService;

    /**
     * 同步支付通道（从 SaaS）
     * XXL-JOB Handler: paymentChannelSync
     */
    @XxlJob("paymentChannelSync")
    public String paymentChannelSync() {
        log.info("XXL-JOB: 开始同步支付通道...");
        try {
            int count = paymentChannelService.syncFromSaas();
            log.info("XXL-JOB: 支付通道同步完成，共 {} 条", count);
            return "支付通道同步成功: " + count + " 条";
        } catch (Exception e) {
            log.error("XXL-JOB: 支付通道同步失败", e);
            return "支付通道同步失败: " + e.getMessage();
        }
    }

    /**
     * 同步商户（从 SaaS）
     * XXL-JOB Handler: merchantSync
     */
    @XxlJob("merchantSync")
    public String merchantSync() {
        log.info("XXL-JOB: 开始同步商户...");
        try {
            int count = merchantService.syncAllFromSaas(null);
            log.info("XXL-JOB: 商户同步完成，共 {} 条", count);
            return "商户同步成功: " + count + " 条";
        } catch (Exception e) {
            log.error("XXL-JOB: 商户同步失败", e);
            return "商户同步失败: " + e.getMessage();
        }
    }

    /**
     * 全量同步（支付通道 + 商户）
     * XXL-JOB Handler: fullSaasSync
     */
    @XxlJob("fullSaasSync")
    public String fullSaasSync() {
        log.info("XXL-JOB: 开始全量同步...");
        StringBuilder result = new StringBuilder();

        // 1. 同步支付通道
        try {
            int channelCount = paymentChannelService.syncFromSaas();
            result.append("支付通道: ").append(channelCount).append(" 条; ");
            log.info("XXL-JOB: 支付通道同步完成，共 {} 条", channelCount);
        } catch (Exception e) {
            result.append("支付通道同步失败: ").append(e.getMessage()).append("; ");
            log.error("XXL-JOB: 支付通道同步失败", e);
        }

        // 2. 同步商户
        try {
            int merchantCount = merchantService.syncAllFromSaas(null);
            result.append("商户: ").append(merchantCount).append(" 条");
            log.info("XXL-JOB: 商户同步完成，共 {} 条", merchantCount);
        } catch (Exception e) {
            result.append("商户同步失败: ").append(e.getMessage());
            log.error("XXL-JOB: 商户同步失败", e);
        }

        log.info("XXL-JOB: 全量同步完成. 结果: {}", result);
        return "全量同步完成. " + result;
    }
}
