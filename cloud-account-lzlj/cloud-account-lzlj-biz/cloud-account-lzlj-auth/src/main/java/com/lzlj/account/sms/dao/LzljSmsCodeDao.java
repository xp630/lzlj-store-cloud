package com.lzlj.account.sms.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.sms.entity.LzljSmsCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信验证码Mapper
 */
@Mapper
public interface LzljSmsCodeDao extends BaseMapper<LzljSmsCode> {
}
