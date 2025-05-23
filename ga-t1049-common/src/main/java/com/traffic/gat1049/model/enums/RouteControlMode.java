package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 干线控制方式枚举
 */
public enum RouteControlMode {
    TIMING_COORDINATED("11", "定时协调"),
    ACTUATED_COORDINATED("12", "感应协调控制"),
    ADAPTIVE_COORDINATED("13", "自适应协调控制");

    private final String code;
    private final String description;

    RouteControlMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static RouteControlMode fromCode(String code) {
        for (RouteControlMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown route control mode code: " + code);
    }
}
