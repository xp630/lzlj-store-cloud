package com.lzlj.account.menu.dto;

import lombok.Data;

/**
 * LZLJ 更新菜单DTO
 */
@Data
public class LzljUpdateMenuDTO {
    private Long parentId;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer type;
    private String permission;
    private Integer status;
}
