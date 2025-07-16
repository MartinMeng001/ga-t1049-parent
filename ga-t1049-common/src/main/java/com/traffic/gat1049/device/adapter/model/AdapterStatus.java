package com.traffic.gat1049.device.adapter.model;

/**
 * 适配器状态枚举
 */
public enum AdapterStatus {

    /**
     * 已创建
     */
    CREATED("已创建"),

    /**
     * 已初始化
     */
    INITIALIZED("已初始化"),

    /**
     * 运行中
     */
    RUNNING("运行中"),

    /**
     * 已停止
     */
    STOPPED("已停止"),

    /**
     * 错误状态
     */
    ERROR("错误状态"),

    /**
     * 已销毁
     */
    DESTROYED("已销毁");

    private final String displayName;

    AdapterStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 检查是否为活动状态
     */
    public boolean isActive() {
        return this == RUNNING;
    }

    /**
     * 检查是否为错误状态
     */
    public boolean isError() {
        return this == ERROR;
    }
}
