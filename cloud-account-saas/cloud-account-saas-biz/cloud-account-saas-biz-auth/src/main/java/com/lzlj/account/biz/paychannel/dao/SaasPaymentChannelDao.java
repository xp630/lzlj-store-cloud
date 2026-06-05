package com.lzlj.account.biz.paychannel.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzlj.account.biz.paychannel.entity.SaasPaymentChannel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 支付通道 Mapper
 */
@Mapper
public interface SaasPaymentChannelDao extends BaseMapper<SaasPaymentChannel> {

    /**
     * 物理删除（不走 @TableLogic）
     */
    @Delete("DELETE FROM saas_auth_payment_channel WHERE id = #{id}")
    void deleteByIdPhysical(@Param("id") Long id);
}
