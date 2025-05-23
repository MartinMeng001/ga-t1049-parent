package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 调度类型枚举
 */
public enum ScheduleType {
    SPECIAL_DAY("1", "特殊日调度（时间段）"),
    WEEK_PERIOD("2", "时间段周调度"),
    WEEK("3", "周调度");

    private final String code;
    private final String description;

    ScheduleType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static ScheduleType fromCode(String code) {
        for (ScheduleType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown schedule type code: " + code);
    }
}
