package com.lzlj.account.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzlj.account.common.core.exception.BusinessException;
import com.lzlj.account.common.core.result.ResultCode;
import com.lzlj.account.user.dto.LzljOrgDTO;
import com.lzlj.account.user.entity.LzljOrg;
import com.lzlj.account.user.entity.LzljUser;
import com.lzlj.account.user.dao.LzljOrgDao;
import com.lzlj.account.user.dao.LzljUserDao;
import com.lzlj.account.user.service.LzljOrgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * LZLJ 机构服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LzljOrgServiceImpl implements LzljOrgService {

    private final LzljOrgDao orgDao;
    private final LzljUserDao userDao;

    @Override
    public Long create(LzljOrg org) {
        // 检查编码唯一性
        LambdaQueryWrapper<LzljOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljOrg::getOrgCode, org.getOrgCode())
               .eq(LzljOrg::getDeleted, 0);
        if (orgDao.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
        }

        // 计算 level 和 level_path
        if (org.getParentId() == null || org.getParentId() == 0) {
            // 根机构
            org.setParentId(0L);
            org.setLevel(1);
            org.setLevelPath("/");
        } else {
            // 子机构，查询父机构
            LzljOrg parent = orgDao.selectById(org.getParentId());
            if (parent == null) {
                throw new BusinessException(ResultCode.DATA_NOT_FOUND);
            }
            org.setLevel(parent.getLevel() + 1);
            org.setLevelPath(parent.getLevelPath());
        }

        org.setStatus(1);
        orgDao.insert(org);

        // 回填 level_path（包含自己的ID）
        String finalLevelPath = org.getLevelPath() + org.getId() + "/";
        org.setLevelPath(finalLevelPath);
        orgDao.updateById(org);

        return org.getId();
    }

    @Override
    public LzljOrgDTO getById(Long id) {
        LzljOrg org = orgDao.selectById(id);
        if (org == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return convertToDTO(org);
    }

    @Override
    public List<LzljOrgDTO> getAllList() {
        LambdaQueryWrapper<LzljOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljOrg::getDeleted, 0)
               .eq(LzljOrg::getStatus, 1)
               .orderByAsc(LzljOrg::getSort);

        return orgDao.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LzljOrgDTO> getTree() {
        List<LzljOrgDTO> allList = getAllList();
        return buildTree(allList, 0L);
    }

    @Override
    public List<LzljOrgDTO> getChildren(Long parentId) {
        LambdaQueryWrapper<LzljOrg> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LzljOrg::getParentId, parentId)
               .eq(LzljOrg::getDeleted, 0)
               .eq(LzljOrg::getStatus, 1)
               .orderByAsc(LzljOrg::getSort);

        return orgDao.selectList(wrapper).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void update(LzljOrg org) {
        LzljOrg existOrg = orgDao.selectById(org.getId());
        if (existOrg == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查编码唯一性（排除自己）
        if (StringUtils.hasText(org.getOrgCode()) && !org.getOrgCode().equals(existOrg.getOrgCode())) {
            LambdaQueryWrapper<LzljOrg> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LzljOrg::getOrgCode, org.getOrgCode())
                   .eq(LzljOrg::getDeleted, 0)
                   .ne(LzljOrg::getId, org.getId());
            if (orgDao.selectCount(wrapper) > 0) {
                throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
            }
        }

        // 不允许修改 parent_id、level、level_path
        org.setParentId(null);
        org.setLevel(null);
        org.setLevelPath(null);

        orgDao.updateById(org);
    }

    @Override
    public void delete(Long id) {
        LzljOrg org = orgDao.selectById(id);
        if (org == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 检查是否有子机构
        LambdaQueryWrapper<LzljOrg> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(LzljOrg::getParentId, id)
                   .eq(LzljOrg::getDeleted, 0);
        if (orgDao.selectCount(childWrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "该机构下存在子机构");
        }

        // 检查是否有用户关联
        LambdaQueryWrapper<LzljUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(LzljUser::getOrgId, id)
                  .eq(LzljUser::getDeleted, 0);
        if (userDao.selectCount(userWrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND.getCode(), "该机构下存在用户");
        }

        // 软删除
        orgDao.deleteById(id);
    }

    /**
     * 构建树形结构
     */
    private List<LzljOrgDTO> buildTree(List<LzljOrgDTO> allList, Long parentId) {
        return allList.stream()
                .filter(org -> org.getParentId().equals(parentId))
                .peek(org -> {
                    List<LzljOrgDTO> children = buildTree(allList, org.getId());
                    org.setChildren(children.isEmpty() ? null : children);
                })
                .collect(Collectors.toList());
    }

    private LzljOrgDTO convertToDTO(LzljOrg org) {
        LzljOrgDTO dto = new LzljOrgDTO();
        BeanUtils.copyProperties(org, dto);
        return dto;
    }
}
