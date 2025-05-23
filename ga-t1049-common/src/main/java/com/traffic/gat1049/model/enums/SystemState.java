package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 状态枚举
 */
public enum SystemState {
    ONLINE("Online", "正常在线"),
    OFFLINE("Offline", "脱机、断线"),
    ERROR("Error", "异常故障");

    private final String code;
    private final String description;

    SystemState(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static SystemState fromCode(String code) {
        for (SystemState state : values()) {
            if (state.code.equals(code)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown system state code: " + code);
    }
}
