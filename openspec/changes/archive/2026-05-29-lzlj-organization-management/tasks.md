## 1. UserContext 增强

- [x] 1.1 在 UserContext 中添加 ORG_ID ThreadLocal
- [x] 1.2 添加 setOrgId(Long) 和 getOrgId() 方法
- [x] 1.3 在 clear() 方法中清除 ORG_ID

## 2. JwtContextFilter 增强

- [x] 2.1 解析 JWT payload 中的 orgId claim
- [x] 2.2 调用 UserContext.setOrgId() 设置机构ID

## 3. 数据库准备

- [x] 3.1 编写 lzlj_auth_org 建表 SQL
- [x] 3.2 执行建表 SQL

## 4. 机构实体与 DAO

- [x] 4.1 创建 LzljOrg.java 实体类（继承 BaseEntity）
- [x] 4.2 创建 LzljOrgDao.java（继承 BaseMapper）

## 5. 机构服务层

- [x] 5.1 创建 LzljOrgService 接口
- [x] 5.2 创建 LzljOrgServiceImpl 实现类
- [x] 5.3 实现 create 方法（自动计算 level 和 level_path）
- [x] 5.4 实现 getById 方法
- [x] 5.5 实现 update 方法
- [x] 5.6 实现 delete 方法（含依赖检查）
- [x] 5.7 实现 getTree 方法（返回完整树）
- [x] 5.8 实现 getChildren 方法（返回子树）
- [x] 5.9 实现 getAllList 方法（扁平列表，供树构建用）

## 6. 机构控制器

- [x] 6.1 创建 LzljOrgController
- [x] 6.2 实现 POST /org（创建机构）
- [x] 6.3 实现 GET /org/tree（获取机构树）
- [x] 6.4 实现 GET /org/{id}（获取详情）
- [x] 6.5 实现 GET /org/{id}/children（获取子机构）
- [x] 6.6 实现 PUT /org（更新机构）
- [x] 6.7 实现 DELETE /org/{id}（删除机构）

## 7. 验证

- [x] 7.1 启动服务验证无报错（编译通过）
- [x] 7.2 测试创建根机构
- [x] 7.3 测试创建子机构（验证 level_path 自动计算）
- [x] 7.4 测试获取机构树
- [x] 7.5 测试 JWT 中 orgId 是否正确解析到 UserContext
