package com.traffic.gat1049.model.enums;

/**
 * 操作命令名称枚举
 * 对应文档表A.3
 */
public enum OperationName {
    LOGIN("Login", "登录"),
    LOGOUT("Logout", "登出"),
    SUBSCRIBE("Subscribe", "订阅"),
    UNSUBSCRIBE("Unsubscribe", "取消订阅"),
    GET("Get", "查询，获取"),
    SET("Set", "设置"),
    NOTIFY("Notify", "通知"),
    OTHER("Other", "其他");

    private final String code;
    private final String description;

    OperationName(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static OperationName fromCode(String code) {
        for (OperationName name : values()) {
            if (name.code.equals(code)) {
                return name;
            }
        }
        return OTHER;
    }
}
