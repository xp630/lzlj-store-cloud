package com.lzlj.account.paymentchannel.dto;

import com.lzlj.account.common.core.domain.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 支付通道查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "支付通道查询条件")
public class LzljPaymentChannelQueryDTO extends PageQueryDTO {

    @Schema(description = "通道编码")
    private String channelCode;

    @Schema(description = "通道名称")
    private String channelName;

    @Schema(description = "通道类型")
    private String channelType;

    @Schema(description = "状态")
    private Integer status;
}
