package com.traffic.gat1049.device.adapter.model;

/**
 * GA/T1049协议配置类型枚举
 * 定义所有支持的配置参数类型
 *
 * @author GA/T1049 Framework
 * @version 1.0
 */
public enum ConfigType {

    /**
     * 阶段参数
     */
    STAGE_PARAM("STAGE_PARAM", "阶段参数", "StageParam"),

    /**
     * 配时方案参数
     */
    PLAN_PARAM("PLAN_PARAM", "配时方案参数", "PlanParam"),

    /**
     * 日计划参数
     */
    DAY_PLAN_PARAM("DAY_PLAN_PARAM", "日计划参数", "DayPlanParam"),

    /**
     * 调度参数
     */
    SCHEDULE_PARAM("SCHEDULE_PARAM", "调度参数", "ScheduleParam"),

    /**
     * 信号组参数
     */
    SIGNAL_GROUP_PARAM("SIGNAL_GROUP_PARAM", "信号组参数", "SignalGroupParam"),

    /**
     * 路口参数
     */
    CROSS_PARAM("CROSS_PARAM", "路口参数", "CrossParam"),

    /**
     * 检测器参数
     */
    DETECTOR_PARAM("DETECTOR_PARAM", "检测器参数", "DetectorParam"),

    /**
     * 通道参数
     */
    CHANNEL_PARAM("CHANNEL_PARAM", "通道参数", "ChannelParam"),

    /**
     * 灯组参数
     */
    LAMP_GROUP_PARAM("LAMP_GROUP_PARAM", "灯组参数", "LampGroupParam"),

    /**
     * 系统参数
     */
    SYSTEM_PARAM("SYSTEM_PARAM", "系统参数", "SystemParam"),

    /**
     * 区域参数
     */
    REGION_PARAM("REGION_PARAM", "区域参数", "RegionParam"),

    /**
     * 子区域参数
     */
    SUB_REGION_PARAM("SUB_REGION_PARAM", "子区域参数", "SubRegionParam"),

    /**
     * 路线参数
     */
    ROUTE_PARAM("ROUTE_PARAM", "路线参数", "RouteParam"),

    /**
     * 时段参数
     */
    PERIOD_PARAM("PERIOD_PARAM", "时段参数", "PeriodParam"),

    /**
     * 混合配置参数（多种类型的组合）
     */
    MIXED_PARAM("MIXED_PARAM", "混合配置参数", "MixedParam");

    /**
     * 配置类型代码
     */
    private final String code;

    /**
     * 配置类型描述
     */
    private final String description;

    /**
     * 对应的实体类名
     */
    private final String entityClass;

    ConfigType(String code, String description, String entityClass) {
        this.code = code;
        this.description = description;
        this.entityClass = entityClass;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getEntityClass() {
        return entityClass;
    }

    /**
     * 根据代码获取配置类型
     */
    public static ConfigType fromCode(String code) {
        for (ConfigType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown config type code: " + code);
    }

    /**
     * 根据实体类名获取配置类型
     */
    public static ConfigType fromEntityClass(String entityClass) {
        for (ConfigType type : values()) {
            if (type.entityClass.equals(entityClass)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown entity class: " + entityClass);
    }

    /**
     * 检查是否为核心配置类型（阶段、方案、日计划、调度）
     */
    public boolean isCoreConfig() {
        return this == STAGE_PARAM || this == PLAN_PARAM ||
                this == DAY_PLAN_PARAM || this == SCHEDULE_PARAM;
    }

    /**
     * 检查是否为系统级配置
     */
    public boolean isSystemConfig() {
        return this == SYSTEM_PARAM || this == REGION_PARAM ||
                this == SUB_REGION_PARAM || this == ROUTE_PARAM;
    }

    /**
     * 检查是否为设备级配置
     */
    public boolean isDeviceConfig() {
        return this == CROSS_PARAM || this == DETECTOR_PARAM ||
                this == CHANNEL_PARAM || this == LAMP_GROUP_PARAM ||
                this == SIGNAL_GROUP_PARAM;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", description, code);
    }
}
