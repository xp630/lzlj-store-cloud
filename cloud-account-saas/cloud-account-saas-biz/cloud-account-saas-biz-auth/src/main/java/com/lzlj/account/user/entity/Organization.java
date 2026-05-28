package com.lzlj.account.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.TenantEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织实体（经销商/门店）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_org")
public class Organization extends TenantEntity {

    /**
     * 组织编码
     */
    private String orgCode;

    /**
     * 组织名称
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
