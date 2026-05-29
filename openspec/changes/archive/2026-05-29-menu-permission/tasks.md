## 1. LZLJ MenuDTO 添加 checked 字段

- [x] 1.1 在 LzljMenuDTO 中添加 `checked` 字段（Boolean类型）

## 2. LZLJ LzljMenuService

- [x] 2.1 添加 `getMyMenus()` 方法 - 获取当前用户可访问菜单
- [x] 2.2 添加 `getAllMenusWithChecked(Long roleId)` 方法 - 获取全部菜单带授权状态
- [x] 2.3 添加构建菜单树的私有方法 `buildMenuTree()`

## 3. LZLJ LzljMenuController

- [x] 3.1 添加 `GET /menu/my` 接口 - 获取当前用户菜单
- [x] 3.2 添加 `GET /menu/all` 接口 - 获取全部菜单（带授权状态）

## 4. LZLJ LzljRoleService

- [x] 4.1 添加 `getRoleMenusTree(Long roleId)` 方法 - 获取角色已授权菜单（树形）

## 5. LZLJ LzljRoleController

- [x] 5.1 添加 `GET /role/{id}/menus/tree` 接口 - 获取角色菜单树

## 6. SAAS MenuDTO 添加 checked 字段

- [x] 6.1 在 MenuDTO 中添加 `checked` 字段

## 7. SAAS MenuService

- [x] 7.1 添加 `getMyMenus()` 方法
- [x] 7.2 添加 `getAllMenusWithChecked(Long roleId)` 方法
- [x] 7.3 添加构建菜单树的私有方法

## 8. SAAS MenuController

- [x] 8.1 添加 `GET /menu/my` 接口
- [x] 8.2 添加 `GET /menu/all` 接口

## 9. SAAS RoleService

- [x] 9.1 添加 `getRoleMenusTree(Long roleId)` 方法

## 10. SAAS RoleController

- [x] 10.1 添加 `GET /role/{id}/menus/tree` 接口

## 11. 测试验证

- [x] 11.1 测试 LZLJ /menu/my 接口 ✅
- [x] 11.2 测试 LZLJ /menu/all 接口 ✅
- [x] 11.3 测试 LZLJ /role/{id}/menus/tree 接口 ✅
- [x] 11.4 测试 SAAS 对应接口 ✅ (注：SAAS /menu/my 需要 SAAS auth 添加 JwtContextFilter)
