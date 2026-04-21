package com.lzlj.store.user.service;

import com.lzlj.store.common.core.domain.PageResult;
import com.lzlj.store.user.dto.UserLoginDTO;
import com.lzlj.store.user.entity.User;
import com.lzlj.store.user.vo.UserVO;

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
    UserVO getCurrentUser();

    /**
     * 根据ID获取用户
     */
    UserVO getById(Long id);

    /**
     * 分页查询用户
     */
    PageResult<UserVO> page(Long orgId, String keyword, Integer status, Integer pageNum, Integer pageSize);

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
}
