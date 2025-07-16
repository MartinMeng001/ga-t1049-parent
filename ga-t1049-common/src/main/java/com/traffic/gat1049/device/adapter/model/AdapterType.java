package com.traffic.gat1049.device.adapter.model;

public enum AdapterType {

    /**
     * 信号机适配器
     */
    SIGNAL_CONTROLLER("信号机适配器", "用于连接和控制交通信号机设备"),

    /**
     * 检测器适配器
     */
    DETECTOR("检测器适配器", "用于连接和管理交通检测器设备"),

    /**
     * 显示屏适配器
     */
    DISPLAY("显示屏适配器", "用于连接和控制交通显示屏设备"),

    /**
     * 摄像头适配器
     */
    CAMERA("摄像头适配器", "用于连接和管理交通监控摄像头"),

    /**
     * 通用设备适配器
     */
    GENERIC("通用设备适配器", "用于连接各种通用交通设备");

    private final String displayName;
    private final String description;

    AdapterType(String displayName, String description) {
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
