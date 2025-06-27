package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 干线控制方式枚举
 */
public enum RouteControlMode {
    NONE_COORDINATED("00", "未进行干线协调控制"),
    FIX_COORDINATED("11", "固定方案协调控制"),
    ADAPTIVE_COORDINATED("12", "自适应协调控制");

    private final String code;
    private final String description;

    RouteControlMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static RouteControlMode fromCode(String code) {
        for (RouteControlMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown route control mode code: " + code);
    }
}
