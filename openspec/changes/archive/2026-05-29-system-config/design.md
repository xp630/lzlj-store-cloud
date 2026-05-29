## 系统参数管理 (SystemParameter)

### 数据结构

```
SystemParameter (继承 BaseEntity)
├── id: Long (主键，自增)
├── paramKey: String (参数编码，全局唯一)
├── paramName: String (参数名称)
├── paramValue: String (参数值)
├── paramType: String (类型：STRING/INTEGER/BOOLEAN/DECIMAL)
├── status: Integer (状态：0-禁用，1-启用)
├── remark: String (备注)
└── BaseEntity 字段 (createTime, updateTime, createBy, updateBy, deleted, version)
```

### 数据字典 (DataDictionary)

```
DataDictionary (继承 BaseEntity)
├── id: Long (主键，自增)
├── dictCode: String (字典编码，全局唯一)
├── dictType: String (字典类型，用于分组)
├── dictLabel: String (字典标签，显示名称)
├── dictValue: String (字典值，存储值)
├── sort: Integer (排序)
├── status: Integer (状态：0-禁用，1-启用)
├── remark: String (备注)
└── BaseEntity 字段
```

### API 设计

#### 系统参数
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /system-parameter | 创建参数 |
| PUT | /system-parameter/{id} | 更新参数 |
| DELETE | /system-parameter/{id} | 删除参数 |
| GET | /system-parameter/{id} | 获取参数详情 |
| GET | /system-parameter/page | 分页查询参数 |
| GET | /system-parameter/list | 参数列表 |
| GET | /system-parameter/key/{key} | 根据 key 获取参数 |

#### 数据字典
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /data-dictionary | 创建字典项 |
| PUT | /data-dictionary/{id} | 更新字典项 |
| DELETE | /data-dictionary/{id} | 删除字典项 |
| GET | /data-dictionary/{id} | 获取字典项详情 |
| GET | /data-dictionary/page | 分页查询字典项 |
| GET | /data-dictionary/list | 字典项列表 |
| GET | /data-dictionary/type/{type} | 根据类型获取字典项 |
| GET | /data-dictionary/all-group | 获取所有字典类型分组 |

### 项目结构

```
cloud-account-saas/cloud-account-saas-biz/cloud-account-saas-biz-auth/src/main/java/com/lzlj/account/
├── system-parameter/
│   ├── entity/SystemParameter.java
│   ├── dao/SystemParameterDao.java
│   ├── service/SystemParameterService.java
│   ├── service/impl/SystemParameterServiceImpl.java
│   ├── controller/SystemParameterController.java
│   └── dto/
│       ├── SystemParameterDTO.java
│       ├── CreateSystemParameterDTO.java
│       ├── UpdateSystemParameterDTO.java
│       └── SystemParameterQueryDTO.java
├── data-dictionary/
│   ├── entity/DataDictionary.java
│   ├── dao/DataDictionaryDao.java
│   ├── service/DataDictionaryService.java
│   ├── service/impl/DataDictionaryServiceImpl.java
│   ├── controller/DataDictionaryController.java
│   └── dto/
│       ├── DataDictionaryDTO.java
│       ├── CreateDataDictionaryDTO.java
│       ├── UpdateDataDictionaryDTO.java
│       └── DataDictionaryQueryDTO.java

cloud-account-lzlj/cloud-account-lzlj-biz/cloud-account-lzlj-auth/src/main/java/com/lzlj/account/
├── system-parameter/
│   ├── entity/LzljSystemParameter.java
│   ├── dao/LzljSystemParameterDao.java
│   ├── service/LzljSystemParameterService.java
│   ├── service/impl/LzljSystemParameterServiceImpl.java
│   ├── controller/LzljSystemParameterController.java
│   └── dto/...
├── data-dictionary/
│   ├── entity/LzljDataDictionary.java
│   ├── dao/LzljDataDictionaryDao.java
│   ├── service/LzljDataDictionaryService.java
│   ├── service/impl/LzljDataDictionaryServiceImpl.java
│   ├── controller/LzljDataDictionaryController.java
│   └── dto/...
```

### 数据库表

#### SaaS
```sql
CREATE TABLE `saas_auth_system_parameter` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `param_key` VARCHAR(100) NOT NULL COMMENT '参数编码',
    `param_name` VARCHAR(100) NOT NULL COMMENT '参数名称',
    `param_value` VARCHAR(500) NOT NULL COMMENT '参数值',
    `param_type` VARCHAR(20) DEFAULT 'STRING' COMMENT '参数类型',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    `remark` VARCHAR(500) DEFAULT NULL,
    -- BaseEntity fields
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL,
    `update_by` BIGINT DEFAULT NULL,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    `version` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_param_key` (`param_key`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `saas_auth_data_dictionary` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `dict_code` VARCHAR(100) NOT NULL COMMENT '字典编码',
    `dict_type` VARCHAR(50) NOT NULL COMMENT '字典类型',
    `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
    `dict_value` VARCHAR(100) NOT NULL COMMENT '字典值',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    `remark` VARCHAR(500) DEFAULT NULL,
    -- BaseEntity fields
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL,
    `update_by` BIGINT DEFAULT NULL,
    `deleted` TINYINT NOT NULL DEFAULT 0,
    `version` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_code`),
    KEY `idx_dict_type` (`dict_type`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### LZLJ
```sql
CREATE TABLE `lzlj_auth_system_parameter` (
    -- 同上结构，表名不同
);

CREATE TABLE `lzlj_auth_data_dictionary` (
    -- 同上结构，表名不同
);
```

### 枚举类

```java
// 通用枚举，放在 common-core
public enum ParamTypeEnum {
    STRING("字符串"),
    INTEGER("整数"),
    BOOLEAN("布尔值"),
    DECIMAL("小数");

    private final String name;
    private final String code;
}
```
