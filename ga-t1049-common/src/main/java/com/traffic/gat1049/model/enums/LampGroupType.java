package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 信号灯组类型枚举
 */
public enum LampGroupType {
    MOTOR_VEHICLE("10", "机动车信号灯"),
    MOTOR_STRAIGHT("21", "机动车方向指示信号灯-直行"),
    MOTOR_LEFT("22", "机动车方向指示信号灯-左转"),
    MOTOR_RIGHT("23", "机动车方向指示信号灯-右转"),
    NON_MOTOR("30", "非机动车信号灯"),
    NON_MOTOR_LEFT("32", "非机动车信号灯-左转"),
    PEDESTRIAN("40", "人行横道信号灯"),
    PEDESTRIAN_ENTRANCE("41", "人行横道信号灯-进口"),
    PEDESTRIAN_EXIT("42", "人行横道信号灯-出口"),
    LANE("50", "车道信号灯"),
    TRAM_STRAIGHT("61", "有轨电车信号灯-直行"),
    TRAM_LEFT("62", "有轨电车信号灯-左转"),
    TRAM_RIGHT("63", "有轨电车信号灯-右转"),
    BUS("70", "公交信号灯"),
    U_TURN("80", "掉头信号灯"),
    OTHER("99", "其他");

    private final String code;
    private final String description;

    LampGroupType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static LampGroupType fromCode(String code) {
        for (LampGroupType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown lamp group type code: " + code);
    }
}
