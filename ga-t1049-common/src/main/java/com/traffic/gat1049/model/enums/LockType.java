package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 锁定类型枚举
 */
public enum LockType {
    CURRENT_PLAN("1", "当前方案中放行此流向的阶段（推荐）"),
    SINGLE_ENTRANCE("2", "进口方向单口放行"),
    SIGNAL_GROUP_ONLY("3", "只放行此流向信号组"),
    LOCK_STAGE("4", "锁定指定阶段");

    private final String code;
    private final String description;

    LockType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() { return code; }
    public String getDescription() { return description; }

    @JsonCreator
    public static LockType fromCode(String code) {
        for (LockType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown lock type code: " + code);
    }
}
