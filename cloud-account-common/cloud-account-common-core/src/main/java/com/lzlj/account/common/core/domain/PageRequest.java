package com.lzlj.account.common.core.domain;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

/**
 * 通用分页请求
 */
@Data
public class PageRequest<T> {

    @Parameter(description = "页码，从1开始")
    private Integer pageNum = 1;

    @Parameter(description = "每页数量")
    private Integer pageSize = 10;

    @Parameter(description = "查询条件")
    private T condition;
}
