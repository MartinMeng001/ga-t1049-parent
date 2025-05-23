package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 通信接口枚举
 */
public enum CommMode {
    ETHERNET("1", "以太网"),
    SERIAL("2", "串口"),
    OTHER("9", "其他");

    private final String code;
    private final String description;

    CommMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static CommMode fromCode(String code) {
        for (CommMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown communication mode code: " + code);
    }
}
