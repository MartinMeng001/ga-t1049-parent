package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 信号组灯态枚举
 */
public enum LampStatus {
    OFF("11", "灭灯"),
    RED("21", "红灯"),
    YELLOW("22", "黄灯"),
    GREEN("23", "绿灯"),
    YELLOW_FLASH("32", "黄闪"),
    GREEN_FLASH("33", "绿闪");

    private final String code;
    private final String description;

    LampStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static LampStatus fromCode(String code) {
        for (LampStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown lamp status code: " + code);
    }
}
