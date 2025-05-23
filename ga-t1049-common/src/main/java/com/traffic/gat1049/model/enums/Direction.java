package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
public enum Direction {
    NORTH("1", "北"),
    NORTHEAST("2", "东北"),
    EAST("3", "东"),
    SOUTHEAST("4", "东南"),
    SOUTH("5", "南"),
    SOUTHWEST("6", "西南"),
    WEST("7", "西"),
    NORTHWEST("8", "西北"),
    OTHER("9", "其他");

    private final String code;
    private final String description;

    Direction(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static Direction fromCode(String code) {
        for (Direction direction : values()) {
            if (direction.code.equals(code)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Unknown direction code: " + code);
    }
}