package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 路口形状枚举
 */
public enum CrossFeature {
    NONE("00", "无"),
    CROSS("11", "十字形"),
    T_SHAPE("21", "T形"),
    Y_SHAPE("31", "Y形"),
    FIVE_WAY("32", "五岔路口"),
    ROUNDABOUT("33", "环形交叉口"),
    PEDESTRIAN("34", "行人过街"),
    RAMP("41", "匝道"),
    OTHER("99", "其他");

    private final String code;
    private final String description;

    CrossFeature(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static CrossFeature fromCode(String code) {
        for (CrossFeature feature : values()) {
            if (feature.code.equals(code)) {
                return feature;
            }
        }
        throw new IllegalArgumentException("Unknown cross feature code: " + code);
    }
}
