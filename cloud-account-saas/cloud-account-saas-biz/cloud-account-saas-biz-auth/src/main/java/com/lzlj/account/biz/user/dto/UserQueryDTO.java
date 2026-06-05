package com.lzlj.account.biz.user.dto;

import com.lzlj.account.common.core.domain.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户查询条件")
public class UserQueryDTO extends PageRequest {

    @Schema(description = "关键字（模糊搜索）")
    private String keyWord;

    @Schema(description = "手机号（模糊搜索）")
    private String phone;

    @Schema(description = "状态 0:禁用 1:启用")
    private Integer status;
}
