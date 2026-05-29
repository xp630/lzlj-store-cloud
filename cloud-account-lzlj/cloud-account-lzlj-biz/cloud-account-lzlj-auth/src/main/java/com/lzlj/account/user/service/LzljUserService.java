package com.lzlj.account.user.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.user.dto.LzljUserLoginDTO;
import com.lzlj.account.user.entity.LzljUser;
import com.lzlj.account.user.dto.LzljUserDTO;

/**
 * LZLJ 用户服务接口
 */
public interface LzljUserService {

    /**
     * 用户登录
     */
    String login(LzljUserLoginDTO loginDTO);

    /**
     * 获取当前用户信息
     */
    LzljUserDTO getCurrentUser();

    /**
     * 根据ID获取用户
     */
    LzljUserDTO getById(Long id);

    /**
     * 分页查询用户
     */
    PageResult<LzljUserDTO> page(Long orgId, String keyword, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 创建用户
     */
    Long create(LzljUser user);

    /**
     * 更新用户
     */
    void update(LzljUser user);

    /**
     * 删除用户
     */
    void delete(Long id);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 修改状态
     */
    void changeStatus(Long userId, Integer status);

    /**
     * 更新用户头像
     */
    void updateAvatar(Long userId, String avatar);
}
