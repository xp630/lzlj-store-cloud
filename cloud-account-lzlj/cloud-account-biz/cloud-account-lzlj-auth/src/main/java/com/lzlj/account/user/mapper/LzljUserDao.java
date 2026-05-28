package com.lzlj.account.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.user.entity.LzljUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * LZLJ 用户Mapper
 */
@Mapper
public interface LzljUserDao extends BaseMapper<LzljUser> {
}
