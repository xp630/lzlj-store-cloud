# 实现任务

## SaaS 模块

### 1. 系统参数管理 (SystemParameter)

- [x] 创建实体类 `SystemParameter.java` (继承 BaseEntity)
- [x] 创建 DAO 接口 `SystemParameterDao.java`
- [x] 创建 DTO 类: `SystemParameterDTO.java`, `CreateSystemParameterDTO.java`, `UpdateSystemParameterDTO.java`, `SystemParameterQueryDTO.java`
- [x] 创建 Service 接口 `SystemParameterService.java`
- [x] 创建 Service 实现 `SystemParameterServiceImpl.java`
- [x] 创建 Controller `SystemParameterController.java`
- [x] 创建 SQL 文件 `sql/saas_system_parameter.sql`

### 2. 数据字典管理 (DataDictionary)

- [x] 创建实体类 `DataDictionary.java` (继承 BaseEntity)
- [x] 创建 DAO 接口 `DataDictionaryDao.java`
- [x] 创建 DTO 类: `DataDictionaryDTO.java`, `CreateDataDictionaryDTO.java`, `UpdateDataDictionaryDTO.java`, `DataDictionaryQueryDTO.java`
- [x] 创建 Service 接口 `DataDictionaryService.java`
- [x] 创建 Service 实现 `DataDictionaryServiceImpl.java`
- [x] 创建 Controller `DataDictionaryController.java`
- [x] 创建 SQL 文件 `sql/saas_data_dictionary.sql`

### 3. EntityTableScanner 更新

- [x] 在 `EntityTableScanner.java` 中添加 `com.lzlj.account.systemparameter.entity`
- [x] 在 `EntityTableScanner.java` 中添加 `com.lzlj.account.data_dictionary.entity`

### 4. 通用枚举 (可选)

- [x] 创建 `ParamTypeEnum.java` 枚举类

---

## LZLJ 模块

### 5. 系统参数管理 (LzljSystemParameter)

- [x] 创建实体类 `LzljSystemParameter.java` (继承 BaseEntity)
- [x] 创建 DAO 接口 `LzljSystemParameterDao.java`
- [x] 创建 DTO 类: `LzljSystemParameterDTO.java`, `CreateLzljSystemParameterDTO.java`, `UpdateLzljSystemParameterDTO.java`, `LzljSystemParameterQueryDTO.java`
- [x] 创建 Service 接口 `LzljSystemParameterService.java`
- [x] 创建 Service 实现 `LzljSystemParameterServiceImpl.java`
- [x] 创建 Controller `LzljSystemParameterController.java`
- [x] 创建 SQL 文件 `sql/lzlj_system_parameter.sql`

### 6. 数据字典管理 (LzljDataDictionary)

- [x] 创建实体类 `LzljDataDictionary.java` (继承 BaseEntity)
- [x] 创建 DAO 接口 `LzljDataDictionaryDao.java`
- [x] 创建 DTO 类: `LzljDataDictionaryDTO.java`, `CreateLzljDataDictionaryDTO.java`, `UpdateLzljDataDictionaryDTO.java`, `LzljDataDictionaryQueryDTO.java`
- [x] 创建 Service 接口 `LzljDataDictionaryService.java`
- [x] 创建 Service 实现 `LzljDataDictionaryServiceImpl.java`
- [x] 创建 Controller `LzljDataDictionaryController.java`
- [x] 创建 SQL 文件 `sql/lzlj_data_dictionary.sql`

### 7. EntityTableScanner 更新 (LZLJ)

- [x] 在 LZLJ 的 `EntityTableScanner.java` 中添加相应的包

---

## 测试

- [x] 测试 SaaS 系统参数 CRUD API
- [x] 测试 SaaS 数据字典 CRUD API
- [x] 测试 LZLJ 系统参数 CRUD API
- [x] 测试 LZLJ 数据字典 CRUD API
