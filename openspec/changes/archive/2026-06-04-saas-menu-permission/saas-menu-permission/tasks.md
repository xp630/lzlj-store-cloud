# Sa-Token 菜单级权限控制 - 任务清单

## 1. 基础设施

- [x] 添加 sa-token 依赖到 SaaS auth pom.xml
- [x] 添加 sa-token 依赖到 LZLJ auth pom.xml

## 2. 配置类

- [x] 创建 SaaSSaTokenConfig.java 配置类
- [x] 创建 LZLJSaTokenConfig.java 配置类

## 3. 登录改造

- [x] 创建 PermissionService 权限加载服务
- [x] 改造 SaaSUserServiceImpl.login() 使用 Sa-Token
- [x] 改造 LZLJUserServiceImpl.login() 使用 Sa-Token

## 4. Gateway 改造

- [x] 改造 SaaS JwtAuthFilter 支持 Sa-Token Token
- [x] 改造 LZLJ JwtAuthFilter 支持 Sa-Token Token

## 5. Controller 注解

- [x] SaaS UserController 添加 @SaCheckPermission 注解
- [x] SaaS RoleController 添加 @SaCheckPermission 注解
- [x] SaaS MenuController 添加 @SaCheckPermission 注解
- [x] SaaS MerchantController 添加 @SaCheckPermission 注解
- [x] LZLJ UserController 添加 @SaCheckPermission 注解
- [x] LZLJ RoleController 添加 @SaCheckPermission 注解
- [x] LZLJ MenuController 添加 @SaCheckPermission 注解
- [x] LZLJ MerchantController 添加 @SaCheckPermission 注解

## 6. 集成测试

- [ ] SaaS 登录 + 权限校验测试
- [ ] LZLJ 登录 + 权限校验测试
- [ ] 权限变更后缓存失效测试
