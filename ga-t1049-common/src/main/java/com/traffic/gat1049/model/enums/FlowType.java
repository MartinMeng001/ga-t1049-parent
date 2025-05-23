package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 交通流类型枚举
 */
public enum FlowType {
    PEDESTRIAN("0", "行人"),
    MOTOR_VEHICLE("1", "机动车"),
    NON_MOTOR_VEHICLE("2", "非机动车");

    private final String code;
    private final String description;

    FlowType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static FlowType fromCode(String code) {
        for (FlowType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown flow type code: " + code);
    }
}
