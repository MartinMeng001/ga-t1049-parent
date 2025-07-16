package com.traffic.gat1049.device.adapter.model;

/**
 * 同步状态枚举
 */
enum SyncStatus {
    /**
     * 等待中
     */
    PENDING("等待中"),

    /**
     * 进行中
     */
    IN_PROGRESS("进行中"),

    /**
     * 已完成
     */
    COMPLETED("已完成"),

    /**
     * 部分成功
     */
    PARTIAL_SUCCESS("部分成功"),

    /**
     * 失败
     */
    FAILED("失败"),

    /**
     * 已取消
     */
    CANCELLED("已取消"),

    /**
     * 超时
     */
    TIMEOUT("超时");

    private final String displayName;

    SyncStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
