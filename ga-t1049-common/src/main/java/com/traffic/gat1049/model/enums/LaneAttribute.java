package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 车道属性枚举
 */
public enum LaneAttribute {
    ENTRANCE("0", "路口进口"),
    EXIT("1", "路口出口"),
    RAMP("2", "匝道"),
    SEGMENT("3", "路段车道"),
    OTHER("9", "其他");

    private final String code;
    private final String description;

    LaneAttribute(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static LaneAttribute fromCode(String code) {
        for (LaneAttribute attribute : values()) {
            if (attribute.code.equals(code)) {
                return attribute;
            }
        }
        throw new IllegalArgumentException("Unknown lane attribute code: " + code);
    }
}
