package com.traffic.gat1049.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 锁定类型枚举
 * 更新版本：根据最新规范添加锁定指定阶段类型
 */
public enum LockType {
    /**
     * 匹配当前方案中放行此流向的阶段（推荐）
     */
    CURRENT_PLAN("1", "匹配当前方案中放行此流向的阶段（推荐）"),

    /**
     * 单个进口方向放行
     */
    SINGLE_ENTRANCE("2", "单个进口方向放行"),

    /**
     * 只放行此流向信号组
     */
    SIGNAL_GROUP_ONLY("3", "只放行此流向信号组"),

    /**
     * 锁定指定阶段
     */
    LOCK_STAGE("4", "锁定指定阶段");

    private final String code;
    private final String description;

    LockType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static LockType fromCode(String code) {
        if (code == null) {
            return null;
        }

        for (LockType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown lock type code: " + code);
    }

    /**
     * 从数值码转换
     * @param code 数值码
     * @return LockType
     */
    public static LockType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return fromCode(String.valueOf(code));
    }

    /**
     * 检查是否需要指定阶段号
     * @return true 如果需要指定阶段号
     */
    public boolean requiresStageNo() {
        return this == LOCK_STAGE;
    }

    /**
     * 检查是否应该使用默认阶段号（0）
     * @return true 如果应该使用默认阶段号
     */
    public boolean usesDefaultStageNo() {
        return this == CURRENT_PLAN || this == SINGLE_ENTRANCE || this == SIGNAL_GROUP_ONLY;
    }
}