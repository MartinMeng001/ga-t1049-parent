package com.traffic.gat1049.model.enums;

// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 检测器类型枚举
 */
public enum DetectorType {
    COIL("1", "线圈"),
    VIDEO("2", "视频"),
    MAGNETIC("3", "地磁"),
    MICROWAVE("4", "微波"),
    RFID("5", "汽车电子标识（RFID）"),
    OTHER("9", "其他");

    private final String code;
    private final String description;

    DetectorType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static DetectorType fromCode(String code) {
        for (DetectorType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown detector type code: " + code);
    }
}