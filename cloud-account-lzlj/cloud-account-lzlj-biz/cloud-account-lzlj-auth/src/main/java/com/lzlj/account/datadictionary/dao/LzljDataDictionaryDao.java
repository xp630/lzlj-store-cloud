package com.lzlj.account.datadictionary.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.datadictionary.entity.LzljDataDictionary;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LzljDataDictionaryDao extends BaseMapper<LzljDataDictionary> {

    /**
     * 物理删除（不走 @TableLogic）
     */
    @Delete("DELETE FROM lzlj_auth_data_dictionary WHERE dict_type = #{dictType}")
    void deleteByDictTypePhysical(@Param("dictType") String dictType);
}
