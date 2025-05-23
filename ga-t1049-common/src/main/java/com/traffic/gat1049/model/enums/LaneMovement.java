package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 车道转向枚举
 */
public enum LaneMovement {
    STRAIGHT("11", "直行"),
    LEFT("12", "左转"),
    RIGHT("13", "右转"),
    STRAIGHT_LEFT("21", "直左混行"),
    STRAIGHT_RIGHT("22", "直右混行"),
    LEFT_RIGHT("23", "左右混行"),
    STRAIGHT_LEFT_RIGHT("24", "直左右混行"),
    U_TURN("31", "掉头"),
    U_TURN_LEFT("32", "掉头加左转"),
    U_TURN_STRAIGHT("33", "掉头加直行"),
    U_TURN_RIGHT("34", "掉头加右转"),
    OTHER("99", "其他");

    private final String code;
    private final String description;

    LaneMovement(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static LaneMovement fromCode(String code) {
        for (LaneMovement movement : values()) {
            if (movement.code.equals(code)) {
                return movement;
            }
        }
        throw new IllegalArgumentException("Unknown lane movement code: " + code);
    }
}
