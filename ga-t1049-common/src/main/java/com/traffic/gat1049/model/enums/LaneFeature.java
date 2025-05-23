package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 车道特性枚举
 */
public enum LaneFeature {
    MOTOR("1", "机动车车道"),
    NON_MOTOR("2", "非机动车车道"),
    MIXED("3", "机非混合车道"),
    OTHER("9", "其他");

    private final String code;
    private final String description;

    LaneFeature(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static LaneFeature fromCode(String code) {
        for (LaneFeature feature : values()) {
            if (feature.code.equals(code)) {
                return feature;
            }
        }
        throw new IllegalArgumentException("Unknown lane feature code: " + code);
    }
}