package com.lzlj.account.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.common.core.tenant.TenantContext;
import com.lzlj.account.role.dao.LzljRoleDao;
import com.lzlj.account.role.dto.LzljRoleDTO;
import com.lzlj.account.role.entity.LzljRole;
import com.lzlj.account.user.dao.LzljUserDao;
import com.lzlj.account.user.dao.LzljUserRoleDao;
import com.lzlj.account.user.entity.LzljUser;
import com.lzlj.account.user.entity.LzljUserRole;
import com.lzlj.account.user.service.LzljUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LZLJ 用户角色服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljUserRoleServiceImpl implements LzljUserRoleService {

    private final LzljUserDao userDao;
    private final LzljUserRoleDao userRoleDao;
    private final LzljRoleDao roleDao;

    @Override
    public List<LzljRoleDTO> getUserRoles(Long userId) {
        // 临时忽略租户隔离，获取用户的角色（不受到当前租户限制）
        TenantContext.setIgnoreTenant(true);
        try {
            // 获取用户角色关联
            LambdaQueryWrapper<LzljUserRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LzljUserRole::getUserId, userId);
            List<LzljUserRole> userRoles = userRoleDao.selectList(wrapper);

            if (userRoles.isEmpty()) {
                return new ArrayList<>();
            }

            // 获取角色列表
            List<Long> roleIds = userRoles.stream().map(LzljUserRole::getRoleId).collect(Collectors.toList());
            List<LzljRole> roles = roleDao.selectBatchIds(roleIds);

            return roles.stream().map(this::convertToDTO).collect(Collectors.toList());
        } finally {
            TenantContext.setIgnoreTenant(false);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 检查用户是否存在
        LzljUser user = userDao.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 删除原有角色关联
        LambdaQueryWrapper<LzljUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljUserRole::getUserId, userId);
        userRoleDao.delete(wrapper);

        // 新增角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<LzljUserRole> userRoles = roleIds.stream().map(roleId -> {
                LzljUserRole userRole = new LzljUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                return userRole;
            }).collect(Collectors.toList());

            for (LzljUserRole userRole : userRoles) {
                userRoleDao.insert(userRole);
            }
        }

        log.info("分配用户角色成功: userId={}, roleIds={}", userId, roleIds);
    }

    private LzljRoleDTO convertToDTO(LzljRole role) {
        LzljRoleDTO dto = new LzljRoleDTO();
        BeanUtils.copyProperties(role, dto);
        return dto;
    }
}
