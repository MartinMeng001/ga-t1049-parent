package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 线路类型枚举
 */
public enum RouteType {
    COORDINATED("1", "协调干线"),
    BUS_PRIORITY("2", "公交优先线路"),
    SPECIAL("3", "特勤线路"),
    TRAMLINES("4", "有轨电车线路"),
    EXPRESSWAY("5", "快速路"),
    OTHER("9", "其他");

    private final String code;
    private final String description;

    RouteType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static RouteType fromCode(String code) {
        for (RouteType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown route type code: " + code);
    }
}
