package com.lzlj.account.biz.merchant.dto;

import com.lzlj.account.common.core.domain.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商户查询条件")
public class MerchantQueryDTO extends PageRequest {

    @Schema(description = "关键字（模糊搜索商户名称）")
    private String keyword;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
}
