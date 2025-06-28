package com.traffic.gat1049.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 调度类型枚举（更新版本）
 * 根据最新文档定义，调度类型的优先级由高到低
 */
public enum ScheduleType {

    /**
     * 特殊日调度（StartDay到EndDay标识的1天或多天）
     * 优先级：最高（1）
     */
    SPECIAL_DAY("1", "特殊日调度", 1),

    /**
     * 时间段周调度（StartDay到EndDay中的周几）
     * 优先级：中（2）
     */
    WEEK_PERIOD("2", "时间段周调度", 2),

    /**
     * 周调度
     * 优先级：最低（3）
     */
    WEEK("3", "周调度", 3);

    private final String code;
    private final String description;
    private final int priority;

    ScheduleType(String code, String description, int priority) {
        this.code = code;
        this.description = description;
        this.priority = priority;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    @JsonCreator
    public static ScheduleType fromCode(String code) {
        for (ScheduleType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown schedule type code: " + code);
    }

    /**
     * 根据优先级获取调度类型
     */
    public static ScheduleType fromPriority(int priority) {
        for (ScheduleType type : values()) {
            if (type.priority == priority) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown schedule type priority: " + priority);
    }

    /**
     * 检查是否需要设置周几字段
     */
    public boolean requiresWeekDay() {
        return this == WEEK_PERIOD || this == WEEK;
    }

    /**
     * 检查是否为特殊日调度
     */
    public boolean isSpecialDay() {
        return this == SPECIAL_DAY;
    }

    /**
     * 检查是否为周相关调度
     */
    public boolean isWeekRelated() {
        return this == WEEK_PERIOD || this == WEEK;
    }

    /**
     * 比较优先级（数值越小优先级越高）
     * @param other 另一个调度类型
     * @return 如果当前类型优先级更高返回负数，相等返回0，更低返回正数
     */
    public int comparePriority(ScheduleType other) {
        return Integer.compare(this.priority, other.priority);
    }

    /**
     * 检查当前调度类型是否比另一个优先级更高
     */
    public boolean isHigherPriorityThan(ScheduleType other) {
        return this.priority < other.priority;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", description, code);
    }
}