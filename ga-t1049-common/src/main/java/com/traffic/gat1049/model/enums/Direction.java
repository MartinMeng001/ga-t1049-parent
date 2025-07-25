package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static Direction fromCode(String code) {
        for (Direction direction : values()) {
            if (direction.code.equals(code)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Unknown direction code: " + code);
    }

    public static Direction from5UDirection(String direction){
        if("North".equalsIgnoreCase(direction)){ return NORTH; }
        if("East".equalsIgnoreCase(direction)){ return EAST; }
        if("South".equalsIgnoreCase(direction)){ return SOUTH; }
        if("West".equalsIgnoreCase(direction)){ return WEST; }
        throw new IllegalArgumentException("Unknown 5U direction: " + direction);
    }
}