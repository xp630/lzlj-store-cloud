package com.lzlj.account.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.role.dao.RoleDao;
import com.lzlj.account.role.dto.RoleDTO;
import com.lzlj.account.role.entity.Role;
import com.lzlj.account.user.dao.SaasUserDao;
import com.lzlj.account.user.dao.SaasUserRoleDao;
import com.lzlj.account.user.dto.UserRoleDTO;
import com.lzlj.account.user.entity.SaasUser;
import com.lzlj.account.user.entity.SaasUserRole;
import com.lzlj.account.user.service.SaasUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SaasUserRoleServiceImpl implements SaasUserRoleService {

    private final SaasUserDao userDao;
    private final SaasUserRoleDao userRoleDao;
    private final RoleDao roleDao;

    @Override
    public List<RoleDTO> getUserRoles(Long userId) {
        // 检查用户是否存在
        SaasUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 获取用户角色关联
        LambdaQueryWrapper<SaasUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SaasUserRole::getUserId, userId);
        List<SaasUserRole> userRoles = userRoleDao.selectList(wrapper);

        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取角色列表
        List<Long> roleIds = userRoles.stream().map(SaasUserRole::getRoleId).collect(Collectors.toList());
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(Role::getId, roleIds);
        List<Role> roles = roleDao.selectList(roleWrapper);

        return roles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, UserRoleDTO dto) {
        // 检查用户是否存在
        SaasUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 删除原有角色关联（硬删，避免 @TableLogic + 唯一键 冲突）
        userRoleDao.deleteByUserIdHard(userId);

        // 新增角色关联
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<SaasUserRole> userRoles = dto.getRoleIds().stream().map(roleId -> {
                SaasUserRole userRole = new SaasUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                return userRole;
            }).collect(Collectors.toList());

            for (SaasUserRole userRole : userRoles) {
                userRoleDao.insert(userRole);
            }
        }

        log.info("分配用户角色成功: userId={}, roleIds={}", userId, dto.getRoleIds());
    }

    private RoleDTO convertToDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyProperties(role, dto);
        return dto;
    }
}
