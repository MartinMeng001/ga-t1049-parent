package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 检测位置枚举
 */
public enum DetectorPosition {
    ENTRANCE("1", "进口"),
    EXIT("2", "出口"),
    OTHER("9", "其他");

    private final String code;
    private final String description;

    DetectorPosition(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static DetectorPosition fromCode(String code) {
        for (DetectorPosition position : values()) {
            if (position.code.equals(code)) {
                return position;
            }
        }
        throw new IllegalArgumentException("Unknown detector position code: " + code);
    }
}
