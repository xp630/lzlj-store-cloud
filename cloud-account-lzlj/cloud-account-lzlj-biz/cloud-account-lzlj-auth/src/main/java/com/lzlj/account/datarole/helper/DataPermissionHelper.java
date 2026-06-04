package com.lzlj.account.datarole.helper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzlj.account.common.core.context.UserContext;
import com.lzlj.account.datarole.entity.DataRoleCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 数据权限帮助类
 * 用于将数据角色条件转换为SQL表达式并应用到查询中
 */
@Slf4j
public class DataPermissionHelper {

    /**
     * 动态值Key常量
     */
    public static final String DYNAMIC_ORG_ID = "currentUser.orgId";
    public static final String DYNAMIC_USER_ID = "currentUser.userId";

    /**
     * 操作符映射
     */
    private static final String OPERATOR_EQ = "=";
    private static final String OPERATOR_NE = "!=";
    private static final String OPERATOR_GT = ">";
    private static final String OPERATOR_LT = "<";
    private static final String OPERATOR_GE = ">=";
    private static final String OPERATOR_LE = "<=";
    private static final String OPERATOR_IN = "IN";
    private static final String OPERATOR_LIKE = "LIKE";
    private static final String OPERATOR_BETWEEN = "BETWEEN";

    /**
     * 构建条件表达式字符串
     * 返回的表达式可以直接应用到 QueryWrapper.apply()
     *
     * @param conditions 数据角色条件列表
     * @return SQL条件表达式
     */
    public static String buildConditionExpression(List<DataRoleCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        Integer currentGroup = null;

        for (int i = 0; i < conditions.size(); i++) {
            DataRoleCondition cond = conditions.get(i);

            // 解析条件值
            String value = resolveValue(cond);

            // 解析操作符
            String sqlOperator = mapOperator(cond.getOperator());

            // 构建表达式
            String expr = buildExpression(cond.getFieldName(), sqlOperator, value, cond.getValueType());

            // 处理分组和逻辑运算符
            Integer group = cond.getConditionGroup();
            if (currentGroup == null) {
                currentGroup = group;
                sb.append("(").append(expr);
            } else if (!currentGroup.equals(group)) {
                // 新的分组，用括号包裹之前的条件
                sb.append(") AND (").append(expr);
                currentGroup = group;
            } else {
                // 同组，根据逻辑运算符连接
                String logicalOp = StringUtils.hasText(cond.getLogicalOperator())
                        ? cond.getLogicalOperator().toUpperCase() : "AND";
                sb.append(" ").append(logicalOp).append(" ").append(expr);
            }
        }

        if (sb.length() > 0) {
            sb.append(")");
        }

        return sb.toString();
    }

    /**
     * 应用数据权限条件到QueryWrapper
     *
     * @param wrapper     QueryWrapper
     * @param conditions  数据角色条件列表
     */
    public static void applyDataPermission(QueryWrapper<?> wrapper, List<DataRoleCondition> conditions) {
        String expr = buildConditionExpression(conditions);
        if (StringUtils.hasText(expr)) {
            wrapper.apply(expr);
        }
    }

    /**
     * 解析条件值
     * 如果是动态值，从上下文获取；否则返回固定值
     */
    private static String resolveValue(DataRoleCondition cond) {
        // 动态值
        if (cond.getValueType() != null && cond.getValueType() == 2) {
            String dynamicKey = cond.getDynamicValueKey();
            if (DYNAMIC_ORG_ID.equals(dynamicKey)) {
                Long orgId = UserContext.getOrgId();
                return orgId != null ? String.valueOf(orgId) : null;
            } else if (DYNAMIC_USER_ID.equals(dynamicKey)) {
                Long userId = UserContext.getUserId();
                return userId != null ? String.valueOf(userId) : null;
            }
            return null;
        }
        // 固定值
        return cond.getFieldValue();
    }

    /**
     * 构建单个条件表达式
     */
    private static String buildExpression(String fieldName, String operator, String value, Integer valueType) {
        if (!StringUtils.hasText(fieldName) || !StringUtils.hasText(operator)) {
            return "";
        }

        // 处理NULL值
        if (value == null) {
            if (OPERATOR_EQ.equals(operator)) {
                return fieldName + " IS NULL";
            } else if (OPERATOR_NE.equals(operator)) {
                return fieldName + " IS NOT NULL";
            }
            return "";
        }

        switch (operator) {
            case OPERATOR_IN:
                return fieldName + " IN (" + value + ")";
            case OPERATOR_LIKE:
                return fieldName + " LIKE '%" + value + "%'";
            case OPERATOR_BETWEEN:
                // BETWEEN 需要两个值，格式: value1 AND value2
                return fieldName + " BETWEEN " + parseBetweenValue(value, true) + " AND " + parseBetweenValue(value, false);
            default:
                // 其他操作符直接使用
                if (isNumeric(value)) {
                    return fieldName + " " + operator + " " + value;
                } else {
                    return fieldName + " " + operator + " '" + value + "'";
                }
        }
    }

    /**
     * 解析BETWEEN值
     */
    private static String parseBetweenValue(String value, boolean isStart) {
        if (value == null || !value.contains(",")) {
            return value;
        }
        String[] parts = value.split(",");
        if (isStart) {
            return parts[0].trim();
        } else {
            return parts.length > 1 ? parts[1].trim() : parts[0].trim();
        }
    }

    /**
     * 判断是否为数值
     */
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 映射操作符
     */
    private static String mapOperator(String operator) {
        if (operator == null) {
            return OPERATOR_EQ;
        }
        switch (operator.toUpperCase()) {
            case "EQ":
            case "等于":
                return OPERATOR_EQ;
            case "NE":
            case "不等于":
                return OPERATOR_NE;
            case "GT":
            case "大于":
                return OPERATOR_GT;
            case "LT":
            case "小于":
                return OPERATOR_LT;
            case "GE":
            case "大于等于":
                return OPERATOR_GE;
            case "LE":
            case "小于等于":
                return OPERATOR_LE;
            case "IN":
                return OPERATOR_IN;
            case "LIKE":
                return OPERATOR_LIKE;
            case "BETWEEN":
                return OPERATOR_BETWEEN;
            default:
                return operator;
        }
    }
}
