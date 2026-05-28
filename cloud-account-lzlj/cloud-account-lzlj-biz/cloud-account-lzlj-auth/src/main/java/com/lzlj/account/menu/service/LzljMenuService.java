package com.lzlj.account.menu.service;

import com.lzlj.account.menu.dto.LzljCreateMenuDTO;
import com.lzlj.account.menu.dto.LzljMenuDTO;
import com.lzlj.account.menu.dto.LzljUpdateMenuDTO;

import java.util.List;

/**
 * LZLJ 菜单服务接口
 */
public interface LzljMenuService {

    /**
     * 创建菜单
     */
    Long create(LzljCreateMenuDTO dto);

    /**
     * 更新菜单
     */
    void update(Long id, LzljUpdateMenuDTO dto);

    /**
     * 删除菜单
     */
    void delete(Long id);

    /**
     * 获取菜单详情
     */
    LzljMenuDTO getById(Long id);

    /**
     * 获取菜单树
     */
    List<LzljMenuDTO> getTree();

    /**
     * 获取菜单列表（平铺）
     */
    List<LzljMenuDTO> getList();

    /**
     * 获取父菜单下拉列表
     */
    List<LzljMenuDTO> getParentMenuList();
}
