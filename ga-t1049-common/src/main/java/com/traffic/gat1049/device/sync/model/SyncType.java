package com.traffic.gat1049.device.sync.model;

/**
 * 同步类型枚举
 */
public enum SyncType {

    /**
     * 配置同步到设备
     */
    CONFIG_TO_DEVICE("配置下发", "将数据库配置同步到设备"),

    /**
     * 状态同步到数据库
     */
    STATUS_FROM_DEVICE("状态上传", "将设备状态同步到数据库"),

    /**
     * 双向数据同步
     */
    BIDIRECTIONAL_SYNC("双向同步", "配置和状态双向同步"),

    /**
     * 批量配置同步
     */
    BATCH_CONFIG_SYNC("批量配置同步", "批量同步多个设备配置"),

    /**
     * 实时数据采集
     */
    REALTIME_DATA_COLLECTION("实时数据采集", "采集设备实时运行数据");

    private final String displayName;
    private final String description;

    SyncType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
