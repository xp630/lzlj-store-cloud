package com.lzlj.account.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.lzlj.account.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * LZLJ 机构实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("lzlj_auth_org")
public class LzljOrg extends BaseEntity {

    /**
     * 机构编码
     */
    private String orgCode;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 组织类型 1:总代理 2:省代 3:市代 4:门店
     */
    private Integer orgType;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 层级路径 如: /1/2/3/
     */
    private String levelPath;

    /**
     * 层级深度
     */
    private Integer level;

    /**
     * 省代码
     */
    private String provinceCode;

    /**
     * 市代码
     */
    private String cityCode;

    /**
     * 区代码
     */
    private String districtCode;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;
}
