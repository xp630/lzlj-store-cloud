package com.lzlj.account.user.service;

import com.lzlj.account.user.dto.LzljOrgDTO;
import com.lzlj.account.user.entity.LzljOrg;

import java.util.List;

/**
 * LZLJ 机构服务接口
 */
public interface LzljOrgService {

    /**
     * 创建机构
     */
    Long create(LzljOrg org);

    /**
     * 根据ID获取机构
     */
    LzljOrgDTO getById(Long id);

    /**
     * 获取所有机构列表（扁平）
     */
    List<LzljOrgDTO> getAllList();

    /**
     * 获取机构树
     */
    List<LzljOrgDTO> getTree();

    /**
     * 获取子机构列表
     */
    List<LzljOrgDTO> getChildren(Long parentId);

    /**
     * 更新机构
     */
    void update(LzljOrg org);

    /**
     * 删除机构
     */
    void delete(Long id);
}
