package com.lzlj.account.user.dto;

import com.lzlj.account.common.core.domain.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ用户查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "LZLJ用户查询条件")
public class LzljUserQueryDTO extends PageRequest {

    @Schema(description = "机构ID")
    private Long orgId;

    @Schema(description = "关键字（模糊搜索用户名）")
    private String keyword;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
}
