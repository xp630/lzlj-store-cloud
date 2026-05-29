## Why

前端需要根据当前用户显示可访问的菜单树，以及管理员在给角色分配权限时需要看到全部菜单并勾选已授权的项。当前系统缺少这两个关键接口。

## What Changes

1. **获取当前用户可访问菜单** `GET /menu/my`
   - 根据用户角色计算可访问菜单
   - 返回树形结构供前端侧边栏渲染

2. **yi** `GET /role/{id}/menus/tree`
   - 返回树形结构代替扁平列表
   - 便于前端渲染权限分配界面的勾选框

3. **获取全部可授权菜单（带勾选状态）** `GET /menu/all?roleId={roleId}`
   - 返回完整菜单树
   - 标注每个菜单是否已授权给指定角色
   - 用于角色权限分配界面

## Capabilities

### New Capabilities
- `user-menu-access`: 获取当前用户可访问菜单（根据用户角色计算）
- `role-menu-tree`: 获取角色已授权菜单（树形结构）
- `menu-permission-assign`: 获取全部菜单并标注授权状态（用于权限分配）

### Modified Capabilities
- （无）

## Impact

- **SAAS**: `MenuService`, `RoleService` 新增方法
- **LZLJ**: `LzljMenuService`, `LzljRoleService` 新增方法
- **API**:
  - `GET /menu/my` - 当前用户菜单
  - `GET /role/{id}/menus/tree` - 角色菜单树
  - `GET /menu/all` - 全部菜单（带授权状态）
