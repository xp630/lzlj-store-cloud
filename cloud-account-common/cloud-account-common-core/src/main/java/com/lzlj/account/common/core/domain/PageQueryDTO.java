package com.lzlj.account.common.core.domain;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页查询基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageQueryDTO extends PageRequest {

    @Parameter(description = "页码，从1开始")
    private Integer pageNum = 1;

    @Parameter(description = "每页数量")
    private Integer pageSize = 10;
}
