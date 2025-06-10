package com.traffic.gat1049.model.enums;

/**
 * 基础应用系统类型标识枚举
 * 对应文档表A.2
 */
public enum SystemType {
    TICP("TICP", "公安交通集成指挥平台"),
    UTCS("UTCS", "交通信号控制系统"),  // 注意：这里使用UTCS而不是TSC
    TVMS("TVMS", "交通视频监视系统"),
    TICS("TICS", "交通流信息采集系统"),
    TVMR("TVMR", "交通违法监测记录系统"),
    TIPS("TIPS", "交通信息发布系统"),
    PGPS("PGPS", "警用车辆与单警定位系统"),
    TDMS("TDMS", "交通设施管理系统"),
    TEDS("TEDS", "交通事件采集系统"),
    VMKS("VMKS", "机动车缉查布控系统");

    private final String code;
    private final String description;

    SystemType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static SystemType fromCode(String code) {
        for (SystemType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown system type code: " + code);
    }
}
