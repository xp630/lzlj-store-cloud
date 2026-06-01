package com.lzlj.account.user.dto;

import lombok.Data;

import java.util.List;

/**
 * LZLJ 机构DTO
 */
@Data
public class LzljOrgDTO {

    private Long id;
    private String orgCode;
    private String orgName;
    private Integer orgType;
    private Long parentId;
    private String levelPath;
    private Integer level;
    private String provinceCode;
    private String cityCode;
    private String districtCode;
    private String address;
    private String contact;
    private String contactPhone;
    private Integer status;
    private Integer sort;

    /**
     * 关联商户ID
     */
    private Long merchantId;

    /**
     * 业务场景ID列表（从顶层母户继承）
     */
    private List<Long> scenarioIds;

    /**
     * 子机构列表（用于树形结构）
     */
    private List<LzljOrgDTO> children;
}
