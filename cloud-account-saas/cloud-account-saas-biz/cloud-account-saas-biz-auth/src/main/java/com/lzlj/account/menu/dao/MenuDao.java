package com.lzlj.account.menu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.menu.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单 Mapper
 */
@Mapper
public interface MenuDao extends BaseMapper<Menu> {
}
