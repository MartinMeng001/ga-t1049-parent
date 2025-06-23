package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 上传数据类型枚举
 */
public enum ReportDataType {
    CROSS_CYCLE("CrossCycle", "路口周期"),
    CROSS_STAGE("CrossStage", "路口阶段"),
    CROSS_SIGNAL_GROUP_STATUS("CrossSignalGroupStatus", "路口信号组灯态"),
    CROSS_TRAFFIC_DATA("CrossTrafficData", "路口交通流数据"),
    STAGE_TRAFFIC_DATA("StageTrafficData", "路口阶段交通流数据");

    private final String code;
    private final String description;

    ReportDataType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static ReportDataType fromCode(String code) {
        for (ReportDataType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown report data type code: " + code);
    }
}
