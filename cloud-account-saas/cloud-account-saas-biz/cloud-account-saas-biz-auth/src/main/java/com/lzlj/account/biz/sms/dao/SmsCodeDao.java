package com.lzlj.account.biz.sms.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.biz.sms.entity.SmsCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信验证码Mapper
 */
@Mapper
public interface SmsCodeDao extends BaseMapper<SmsCode> {
}
