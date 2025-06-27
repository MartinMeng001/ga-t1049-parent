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
    PEDESTRIAN("10", "行人过街"),
    PEDESTRIANSTEP("12", "二次行人过街"),
    CROSS("24", "十字形"),
    T_Y_SHAPE("23", "T形Y形"),
    FIVE_WAY("35", "五岔路口"),
    SIX_WAY("36", "六岔路口"),
    MORE_WAY("39", "多岔路口"),
    ROUNDABOUT("40", "环形交叉口"),
    RAMP("50", "匝道"),
    RAMP_IN("51", "匝道入口"),
    RAMP_OUT("52", "匝道出口"),
    EXPRESSWAY_INTERSECTION("61", "快速路主路路段-交汇区"),
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
