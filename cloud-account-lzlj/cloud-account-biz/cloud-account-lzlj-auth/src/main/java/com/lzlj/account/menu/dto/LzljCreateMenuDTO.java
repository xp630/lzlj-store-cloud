package com.lzlj.account.menu.dto;

import lombok.Data;

/**
 * LZLJ 创建菜单DTO
 */
@Data
public class LzljCreateMenuDTO {
    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer type;
    private String permission;
    private Integer status;
    private Long orgId;
}
