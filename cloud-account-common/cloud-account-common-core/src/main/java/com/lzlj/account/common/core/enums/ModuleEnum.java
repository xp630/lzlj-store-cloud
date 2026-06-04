package com.lzlj.account.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作日志模块枚举
 */
@Getter
@AllArgsConstructor
public enum ModuleEnum {

    USER("用户管理", "user"),
    ROLE("角色管理", "role"),
    MENU("菜单管理", "menu"),
    TENANT("租户管理", "tenant"),
    API_KEY("API密钥", "apiKey"),
    SYSTEM("系统管理", "system");

    private final String description;
    private final String permission;

    public static ModuleEnum getByDescription(String description) {
        if (description == null) {
            return null;
        }
        for (ModuleEnum module : values()) {
            if (module.description.equals(description)) {
                return module;
            }
        }
        return null;
    }
}
