package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 可变车道控制方式枚举
 */
public enum VarLaneMode {
    CONTROLLER("00", "恢复信号机控制"),
    CONTROLLER_FIXED("11", "信号机控制-固定方案"),
    CONTROLLER_ADAPTIVE("12", "信号机控制-自适应"),
    INTERVENTION_FIXED("21", "干预控制-固定方案"),
    INTERVENTION_ADAPTIVE("22", "干预控制-自适应"),
    OTHER("9", "其他");

    private final String code;
    private final String description;

    VarLaneMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static VarLaneMode fromCode(String code) {
        for (VarLaneMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown variable lane mode code: " + code);
    }
}
