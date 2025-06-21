package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 迟开早闭操作枚举
 */
public enum AdjustOperation {
    LATE_START("1", "迟开"),
    EARLY_END("2", "早闭");

    private final String code;
    private final String description;

    AdjustOperation(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static AdjustOperation fromCode(String code) {
        for (AdjustOperation operation : values()) {
            if (operation.code.equals(code)) {
                return operation;
            }
        }
        throw new IllegalArgumentException("Unknown adjust operation code: " + code);
    }
}
