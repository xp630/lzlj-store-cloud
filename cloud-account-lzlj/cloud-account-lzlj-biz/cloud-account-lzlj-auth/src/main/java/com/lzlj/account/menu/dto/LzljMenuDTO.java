package com.lzlj.account.menu.dto;

import lombok.Data;
import java.util.List;

/**
 * LZLJ 菜单DTO
 */
@Data
public class LzljMenuDTO {
    private Long id;
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
    private Boolean checked;
    private List<LzljMenuDTO> children;
}
