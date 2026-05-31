package com.lzlj.account.common.core.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
@Data
@Schema(description = "分页响应结构")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码", example = "1")
    private Long current;

    @Schema(description = "每页记录数", example = "10")
    private Long size;

    @Schema(description = "总记录数", example = "100")
    private Long total;

    @Schema(description = "总页数", example = "10")
    private Long pages;

    @Schema(description = "当前页数据列表")
    private List<T> records;

    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;

    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
        this.hasPrevious = current > 1;
        this.hasNext = current < pages;
    }
}
