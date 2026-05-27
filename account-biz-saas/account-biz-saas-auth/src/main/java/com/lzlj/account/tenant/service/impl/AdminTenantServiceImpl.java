package com.lzlj.account.tenant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.tenant.dao.AdminTenantDao;
import com.lzlj.account.tenant.dao.TenantDao;
import com.lzlj.account.tenant.dto.AdminTenantDTO;
import com.lzlj.account.tenant.dto.AssignTenantDTO;
import com.lzlj.account.tenant.entity.AdminTenant;
import com.lzlj.account.tenant.entity.Tenant;
import com.lzlj.account.tenant.service.AdminTenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员租户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTenantServiceImpl implements AdminTenantService {

    private final AdminTenantDao adminTenantDao;
    private final TenantDao tenantDao;

    @Override
    public List<AdminTenantDTO> getAdminTenants(Long adminUserId) {
        // 获取管理员关联的租户ID列表
        LambdaQueryWrapper<AdminTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminTenant::getAdminUserId, adminUserId);
        List<AdminTenant> adminTenants = adminTenantDao.selectList(wrapper);

        if (adminTenants.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> tenantIds = adminTenants.stream()
                .map(AdminTenant::getTenantId)
                .collect(Collectors.toList());

        // 获取租户信息
        LambdaQueryWrapper<Tenant> tenantWrapper = new LambdaQueryWrapper<>();
        tenantWrapper.in(Tenant::getId, tenantIds)
                     .eq(Tenant::getStatus, 1)
                     .eq(Tenant::getDeleted, 0);
        List<Tenant> tenants = tenantDao.selectList(tenantWrapper);

        return tenants.stream().map(tenant -> {
            AdminTenantDTO dto = new AdminTenantDTO();
            dto.setTenantId(tenant.getId());
            dto.setTenantCode(tenant.getTenantCode());
            dto.setTenantName(tenant.getTenantName());
            dto.setSelected(false);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTenants(Long adminUserId, AssignTenantDTO dto) {
        // 删除原有关联
        LambdaQueryWrapper<AdminTenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminTenant::getAdminUserId, adminUserId);
        adminTenantDao.delete(wrapper);

        // 新增关联
        if (dto.getTenantIds() != null && !dto.getTenantIds().isEmpty()) {
            List<AdminTenant> adminTenants = dto.getTenantIds().stream().map(tenantId -> {
                AdminTenant adminTenant = new AdminTenant();
                adminTenant.setAdminUserId(adminUserId);
                adminTenant.setTenantId(tenantId);
                return adminTenant;
            }).collect(Collectors.toList());

            for (AdminTenant adminTenant : adminTenants) {
                adminTenantDao.insert(adminTenant);
            }
        }

        log.info("分配管理员可管理租户成功: adminUserId={}, tenantIds={}", adminUserId, dto.getTenantIds());
    }
}
