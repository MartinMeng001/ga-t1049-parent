package com.traffic.gat1049.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 通信接口枚举
 * 符合GA/T 1049.2-2016标准表B.7定义
 */
public enum CommMode {
    ETHERNET("10", "以太网"),
    TCP_CLIENT("11", "TCP Client"),
    TCP_SERVER("12", "TCP Server"),
    UDP("13", "UDP"),
    SERIAL("20", "串口"),
    OTHER("99", "其他");

    private final String code;
    private final String description;

    CommMode(String code, String description) {
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
    public static CommMode fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (CommMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown communication mode code: " + code);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", description, code);
    }
}