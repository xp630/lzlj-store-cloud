## Context

系统已有菜单和角色功能，但缺少根据用户角色计算可访问菜单的接口。前端需要：
1. 用户登录后显示可访问的菜单（侧边栏）
2. 管理员给角色分配权限时，显示全部菜单并标注已授权项

当前问题：
- `getRoleMenus()` 返回扁平列表，前端无法直接渲染树形勾选框
- 缺少获取当前用户可访问菜单的接口
- 缺少带授权状态的菜单列表接口

## Goals / Non-Goals

**Goals:**
- 实现 `GET /menu/my` 返回当前用户可访问菜单树
- 实现 `GET /role/{id}/menus/tree` 返回角色已授权菜单（树形）
- 实现 `GET /menu/all?roleId={roleId}` 返回全部菜单（带授权状态）

**Non-Goals:**
- 不修改现有菜单CRUD接口
- 不实现按钮级别的权限控制（只到菜单级别）
- 不实现数据范围的动态过滤（机构维度后续单独考虑）

## Decisions

### 1. 查询链路设计

**选择**: 用户 → 用户角色关联表 → 角色菜单关联表 → 菜单表

```
UserContext.getUserId()
    ↓
lzlj_auth_user_role (WHERE user_id = ?)
    ↓ 获取 role_ids
lzlj_auth_role_menu (WHERE role_id IN ?)
    ↓ 获取 menu_ids
lzlj_auth_menu (WHERE id IN ?)
    ↓
buildTree() → 返回树形
```

### 2. 树形构建策略

**选择**: 内存中构建树形（Java 代码处理）

数据库返回扁平菜单列表，通过递归构建树：
```java
private List<MenuDTO> buildTree(List<Menu> menus, Long parentId) {
    return menus.stream()
        .filter(m -> Objects.equals(m.getParentId(), parentId))
        .map(m -> { dto.setChildren(buildTree(menus, m.getId())); return dto; })
        .collect(toList());
}
```

替代方案：SQL 递归查询（MySQL 8.0+ 支持）→ 放弃，因为数据库兼容性。

### 3. 授权状态标注

**选择**: 在 `MenuDTO` 中添加 `checked` 布尔字段

```java
public class MenuDTO {
    // ... 原有字段
    private Boolean checked;  // 是否已授权
}
```

查询时根据 roleId 获取已授权菜单ID集合，标注到返回结果中。

## Risks / Trade-offs

[Risk] 用户角色过多时查询性能
→ **Mitigation**: 正常场景用户角色有限，如需优化可加缓存

[Risk] 未来支持数据范围过滤时需要重构
→ **Mitigation**: 当前只做菜单级，后续按需扩展

## Open Questions

1. 超级管理员（user_type=1）是否显示全部菜单？
2. 菜单需要按机构隔离吗？（机构维度后续讨论）
