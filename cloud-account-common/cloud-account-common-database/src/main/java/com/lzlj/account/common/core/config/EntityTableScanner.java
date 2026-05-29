package com.lzlj.account.common.core.config;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lzlj.account.common.core.domain.BaseEntity;
import com.lzlj.account.common.core.domain.TenantEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;

/**
 * 实体类与表名扫描器
 * 用于租户拦截器判断表是否需要租户隔离
 *
 * 自动扫描 com.lzlj.account 下所有 *.entity 和 *.domain 包中的 BaseEntity 子类
 */
@Component
public class EntityTableScanner {

    /**
     * 表名 → 是否需要租户隔离（继承 TenantEntity）
     */
    private static final Map<String, Boolean> TABLE_NEED_TENANT = new ConcurrentHashMap<>();

    @PostConstruct
    public void scan() {
        // 扫描所有继承 BaseEntity 的类
        Set<Class<? extends BaseEntity>> entityClasses = scanEntityClasses();
        for (Class<? extends BaseEntity> clazz : entityClasses) {
            TableName tableNameAnnotation = clazz.getAnnotation(TableName.class);
            if (tableNameAnnotation != null) {
                String tableName = tableNameAnnotation.value();
                // 判断是否需要租户隔离：检查是否继承 TenantEntity 或有 tenantId 字段
                boolean needTenant = isTenantEntity(clazz);
                TABLE_NEED_TENANT.put(tableName, needTenant);
                System.out.println("[EntityTableScanner] Registered table: " + tableName + " -> needTenant: " + needTenant + " (" + clazz.getName() + ")");
            }
        }
        System.out.println("[EntityTableScanner] Total tables registered: " + TABLE_NEED_TENANT.size());
    }

    /**
     * 判断实体类是否需要租户隔离
     */
    private boolean isTenantEntity(Class<? extends BaseEntity> clazz) {
        // 检查是否继承自 TenantEntity
        if (TenantEntity.class.isAssignableFrom(clazz)) {
            return true;
        }
        // 备用：检查是否有 tenantId 字段
        try {
            Field tenantIdField = clazz.getDeclaredField("tenantId");
            return tenantIdField != null;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * 判断表是否需要租户隔离
     */
    public static boolean needTenant(String tableName) {
        // 未注册的表默认需要租户隔离（安全考虑）
        return TABLE_NEED_TENANT.getOrDefault(tableName, true);
    }

    /**
     * 扫描所有继承 BaseEntity 的类
     * 自动发现 com.lzlj.account.**.entity 和 com.lzlj.account.**.domain 包
     */
    @SuppressWarnings("unchecked")
    private Set<Class<? extends BaseEntity>> scanEntityClasses() {
        Set<Class<? extends BaseEntity>> classes = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // 从 com.lzlj.account 根包开始扫描，自动发现 entity 和 domain 包
        Set<String> entityPackages = discoverEntityPackages("com.lzlj.account", classLoader);

        System.out.println("[EntityTableScanner] Discovered entity packages: " + entityPackages);

        for (String packageName : entityPackages) {
            scanPackage(classes, packageName, classLoader);
        }

        return classes;
    }

    /**
     * 自动发现所有 *.entity 和 *.domain 包
     */
    private Set<String> discoverEntityPackages(String rootPackage, ClassLoader classLoader) {
        Set<String> packages = new HashSet<>();
        String path = rootPackage.replace('.', '/');

        try {
            java.util.Enumeration<java.net.URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                java.net.URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    File dir = new File(resource.toURI());
                    if (dir.exists()) {
                        discoverPackagesRecursive(dir, rootPackage, packages);
                    }
                }
            }
        } catch (Exception e) {
            // 忽略无法扫描的包
        }

        return packages;
    }

    /**
     * 递归扫描目录，查找 *.entity 和 *.domain 包
     */
    private void discoverPackagesRecursive(File dir, String packageName, Set<String> packages) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                String subPackage = packageName + "." + file.getName();
                // 检查是否是需要扫描的包
                if (file.getName().equals("entity") || file.getName().equals("domain")) {
                    packages.add(subPackage);
                } else {
                    // 继续递归扫描子包
                    discoverPackagesRecursive(file, subPackage, packages);
                }
            }
        }
    }

    /**
     * 扫描指定包下的类
     */
    @SuppressWarnings("unchecked")
    private void scanPackage(Set<Class<? extends BaseEntity>> classes, String packageName, ClassLoader classLoader) {
        String path = packageName.replace('.', '/');
        try {
            java.util.Enumeration<java.net.URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                java.net.URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    File dir = new File(resource.toURI());
                    if (dir.exists()) {
                        scanDirectory(classes, dir, packageName);
                    }
                }
            }
        } catch (Exception e) {
            // 包不存在时忽略
        }
    }

    /**
     * 递归扫描目录下的类文件
     */
    @SuppressWarnings("unchecked")
    private void scanDirectory(Set<Class<? extends BaseEntity>> classes, File dir, String packageName) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(classes, file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (BaseEntity.class.isAssignableFrom(clazz) && !clazz.isInterface() && !clazz.isAnonymousClass()) {
                        classes.add((Class<? extends BaseEntity>) clazz);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    // 忽略无法加载的类
                }
            }
        }
    }
}
