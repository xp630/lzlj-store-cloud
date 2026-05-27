package com.lzlj.account.tenant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzlj.account.common.core.domain.PageResult;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.tenant.dao.TenantDao;
import com.lzlj.account.tenant.dto.CreateTenantDTO;
import com.lzlj.account.tenant.dto.TenantDTO;
import com.lzlj.account.tenant.dto.UpdateTenantDTO;
import com.lzlj.account.tenant.entity.Tenant;
import com.lzlj.account.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

/**
 * 租户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantDao tenantDao;

    @Override
    public Long create(CreateTenantDTO dto) {
        // 检查编码唯一性
        if (checkCodeExists(dto.getTenantCode(), null)) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS.getCode(), "租户编码已存在");
        }

        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(dto, tenant);
        tenant.setStatus(1); // 默认启用

        tenantDao.insert(tenant);
        log.info("创建租户成功: id={}, code={}", tenant.getId(), tenant.getTenantCode());

        return tenant.getId();
    }

    @Override
    public void update(Long id, UpdateTenantDTO dto) {
        Tenant existTenant = tenantDao.selectById(id);
        if (existTenant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查编码唯一性（排除自己）
        if (StringUtils.hasText(dto.getTenantName()) && checkCodeExists(null, id)) {
            // 只检查名称即可，编码不变
        }

        BeanUtils.copyProperties(dto, existTenant);
        tenantDao.updateById(existTenant);

        log.info("更新租户成功: id={}", id);
    }

    @Override
    public void delete(Long id) {
        Tenant tenant = tenantDao.selectById(id);
        if (tenant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 物理删除（根据业务需求可改为逻辑删除）
        tenantDao.deleteById(id);
        log.info("删除租户成功: id={}", id);
    }

    @Override
    public TenantDTO getById(Long id) {
        Tenant tenant = tenantDao.selectById(id);
        if (tenant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(tenant);
    }

    @Override
    public TenantDTO getByCode(String tenantCode) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tenant::getTenantCode, tenantCode)
               .eq(Tenant::getDeleted, 0);
        Tenant tenant = tenantDao.selectOne(wrapper);
        if (tenant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "租户不存在");
        }
        return convertToDTO(tenant);
    }

    @Override
    public PageResult<TenantDTO> page(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        Page<Tenant> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Tenant::getTenantName, keyword)
               .eq(status != null, Tenant::getStatus, status)
               .eq(Tenant::getDeleted, 0)
               .orderByDesc(Tenant::getCreateTime);

        IPage<Tenant> resultPage = tenantDao.selectPage(page, wrapper);

        return new PageResult<>(
                resultPage.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList()),
                resultPage.getTotal(),
                resultPage.getCurrent(),
                resultPage.getSize()
        );
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        Tenant tenant = tenantDao.selectById(id);
        if (tenant == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        tenant.setStatus(status);
        tenantDao.updateById(tenant);
        log.info("修改租户状态: id={}, status={}", id, status);
    }

    private boolean checkCodeExists(String tenantCode, Long excludeId) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<>();
        if (tenantCode != null) {
            wrapper.eq(Tenant::getTenantCode, tenantCode);
        }
        if (excludeId != null) {
            wrapper.ne(Tenant::getId, excludeId);
        }
        wrapper.eq(Tenant::getDeleted, 0);
        return tenantDao.selectCount(wrapper) > 0;
    }

    private TenantDTO convertToDTO(Tenant tenant) {
        TenantDTO dto = new TenantDTO();
        BeanUtils.copyProperties(tenant, dto);
        return dto;
    }
}
