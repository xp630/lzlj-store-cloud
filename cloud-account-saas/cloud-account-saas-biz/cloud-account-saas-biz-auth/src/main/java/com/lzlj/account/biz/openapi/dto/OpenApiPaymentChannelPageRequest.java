package com.lzlj.account.biz.openapi.dto;

import com.lzlj.account.biz.paychannel.dto.PaymentChannelQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * OpenAPI 支付通道分页请求
 */
@Data
@Schema(description = "支付通道分页请求")
public class OpenApiPaymentChannelPageRequest {

    @Schema(description = "通道名称（模糊搜索）")
    private String channelName;

    @Schema(description = "状态（0:禁用 1:启用）")
    private Integer status;

    @Schema(description = "页码", required = true)
    private Integer pageNum = 1;

    @Schema(description = "每页数量", required = true)
    private Integer pageSize = 10;

    public PaymentChannelQueryDTO toQueryDTO() {
        PaymentChannelQueryDTO dto = new PaymentChannelQueryDTO();
        dto.setChannelName(channelName);
        dto.setStatus(status);
        return dto;
    }
}
