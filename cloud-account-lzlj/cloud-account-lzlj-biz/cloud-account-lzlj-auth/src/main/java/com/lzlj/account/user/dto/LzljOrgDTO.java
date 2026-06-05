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
     * 业务场景ID
     */
    private Long scenarioId;

    /**
     * 商户名称（关联查询）
     */
    private String merchantName;

    /**
     * 场景名称（关联查询）
     */
    private String scenarioName;

    /**
     * 上级机构名称（关联查询）
     */
    private String parentOrgName;

    /**
     * 统一社会信用代码
     */
    private String unifiedSocialCreditCode;

    /**
     * 子机构列表（用于树形结构）
     */
    private List<LzljOrgDTO> children;
}
