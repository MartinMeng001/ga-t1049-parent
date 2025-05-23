package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 信号机故障类型枚举
 */
public enum ControllerErrorType {
    LAMP_OUTPUT("1", "灯输出故障"),
    POWER("2", "电源故障"),
    CLOCK("3", "时钟故障"),
    RUNTIME("4", "运行故障"),
    PLAN("5", "方案错误"),
    OTHER("9", "其他错误");

    private final String code;
    private final String description;

    ControllerErrorType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static ControllerErrorType fromCode(String code) {
        for (ControllerErrorType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown controller error type code: " + code);
    }
}
