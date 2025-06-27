package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 信号组灯态枚举
 */
public enum LampStatus {
    NONE("0", "无灯"),
    OFF("1", "灭灯"),
    LIGHT("2", "亮灯"),
    FLASH("3", "闪灯");

    private final String code;
    private final String description;

    LampStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static LampStatus fromCode(String code) {
        for (LampStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown lamp status code: " + code);
    }
}
