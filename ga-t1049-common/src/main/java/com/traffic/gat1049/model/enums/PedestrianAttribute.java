package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 人行横道属性枚举
 */
public enum PedestrianAttribute {
    ONE_STAGE("1", "一次过街"),
    TWO_STAGE_ENTRANCE("21", "二次过街-进口"),
    TWO_STAGE_EXIT("22", "二次过街-出口");

    private final String code;
    private final String description;

    PedestrianAttribute(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static PedestrianAttribute fromCode(String code) {
        for (PedestrianAttribute attribute : values()) {
            if (attribute.code.equals(code)) {
                return attribute;
            }
        }
        throw new IllegalArgumentException("Unknown pedestrian attribute code: " + code);
    }
}
