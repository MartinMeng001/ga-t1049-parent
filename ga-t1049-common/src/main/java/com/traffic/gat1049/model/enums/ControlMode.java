package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 控制方式枚举
 */
public enum ControlMode {
    CANCEL("00", "撤销，恢复自主"),
    LOCAL_MANUAL("01", "本地手控"),
    LIGHT_OFF("11", "特殊控制-关灯"),
    ALL_RED("12", "特殊控制-全红"),
    ALL_YELLOW_FLASH("13", "特殊控制-全部黄闪"),
    SINGLE_TIMING("21", "单点定时控制"),
    SINGLE_ACTUATED("22", "单点感应控制"),
    SINGLE_ADAPTIVE("23", "单点自适应控制"),
    LINE_COORDINATED("31", "线协调控制"),
    LINE_COORDINATED_ACTUATED("32", "线协调感应控制"),
    LINE_COORDINATED_ADAPTIVE("33", "线协调自适应控制"),
    AREA_COORDINATED("41", "区域协调控制"),
    MANUAL("51", "干预控制-手动控制"),
    STAGE_LOCK("52", "干预控制-锁定阶段控制"),
    PLAN_LOCK("53", "干预控制-指定方案");

    private final String code;
    private final String description;

    ControlMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static ControlMode fromCode(String code) {
        for (ControlMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown control mode code: " + code);
    }
}
