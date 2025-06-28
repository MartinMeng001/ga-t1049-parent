package com.traffic.gat1049.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 机柜门状态枚举
 * 符合GA/T 1049.2-2024标准，表B.41
 */
public enum DoorStatus {
    CLOSED("0", "关闭"),
    OPEN("1", "打开"),
    UNKNOWN("9", "未知");

    private final String code;
    private final String description;

    DoorStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static DoorStatus fromCode(String code) {
        for (DoorStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown door status code: " + code);
    }

    @Override
    public String toString() {
        return getDescription();
    }
}