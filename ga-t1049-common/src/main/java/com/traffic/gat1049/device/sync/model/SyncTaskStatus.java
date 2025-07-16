package com.traffic.gat1049.device.sync.model;

/**
 * 同步任务状态枚举
 */
public enum SyncTaskStatus {

    /**
     * 已创建
     */
    CREATED("已创建", false),

    /**
     * 等待执行
     */
    PENDING("等待执行", false),

    /**
     * 执行中
     */
    RUNNING("执行中", false),

    /**
     * 已完成
     */
    COMPLETED("已完成", true),

    /**
     * 失败
     */
    FAILED("失败", true),

    /**
     * 已取消
     */
    CANCELLED("已取消", true),

    /**
     * 超时
     */
    TIMEOUT("超时", true);

    private final String displayName;
    private final boolean completed;

    SyncTaskStatus(String displayName, boolean completed) {
        this.displayName = displayName;
        this.completed = completed;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCompleted() {
        return completed;
    }
}
