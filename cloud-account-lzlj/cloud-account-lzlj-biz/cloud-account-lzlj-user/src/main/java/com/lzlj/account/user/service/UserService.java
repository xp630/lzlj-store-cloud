package com.lzlj.account.user.service;

import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.user.dto.UserLoginDTO;
import com.lzlj.account.user.entity.User;
import com.lzlj.account.user.dto.UserDTO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     */
    String login(UserLoginDTO loginDTO);

    /**
     * 获取当前用户信息
     */
    UserDTO getCurrentUser();

    /**
     * 根据ID获取用户
     */
    UserDTO getById(Long id);

    /**
     * 分页查询用户
     */
    PageResult<UserDTO> page(Long orgId, String keyword, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 创建用户
     */
    Long create(User user);

    /**
     * 更新用户
     */
    void update(User user);

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
     * 绑定微信
     */
    void bindWx(Long userId, String wxOpenid, String wxMaOpenid);

    /**
     * 更新用户头像
     */
    void updateAvatar(Long userId, String avatar);
}
