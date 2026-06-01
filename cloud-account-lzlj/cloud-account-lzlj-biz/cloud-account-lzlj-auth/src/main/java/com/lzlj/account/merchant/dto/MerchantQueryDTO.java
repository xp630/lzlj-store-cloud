package com.lzlj.account.merchant.dto;

import com.lzlj.account.common.core.domain.PageQueryDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 商户查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商户查询条件")
public class MerchantQueryDTO extends PageQueryDTO {

    @Schema(description = "商户名称（模糊搜索）")
    private String merchantName;

    @Schema(description = "商户编号")
    private String merchantCode;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "状态")
    private Integer status;
}
