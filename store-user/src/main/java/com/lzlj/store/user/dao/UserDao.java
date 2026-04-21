package com.lzlj.store.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.store.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface UserDao extends BaseMapper<User> {
}
