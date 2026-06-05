package com.lzlj.account.biz.datadictionary.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.biz.datadictionary.entity.DataDictionary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典 Mapper
 */
@Mapper
public interface DataDictionaryDao extends BaseMapper<DataDictionary> {
}
