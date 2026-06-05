package com.lzlj.account.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作日志模块枚举
 */
@Getter
@AllArgsConstructor
public enum ModuleEnum {

    USER("用户管理", "USER"),
    ROLE("角色管理", "ROLE"),
    MENU("菜单管理", "MENU"),
    TENANT("租户管理", "TENANT"),
    APIKEY("API密钥", "APIKEY"),
    SYSTEM("系统管理", "SYSTEM");

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
