package com.lzlj.lzlj.goods.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品 VO
 */
@Data
public class GoodsVO {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private String unit;
    private String description;
    private String imageUrl;
    private Integer status;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
