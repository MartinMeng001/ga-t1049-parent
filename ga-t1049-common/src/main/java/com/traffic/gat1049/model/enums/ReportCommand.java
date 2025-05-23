package com.traffic.gat1049.model.enums;
// ========== 基础枚举类 ==========

/**
 * 方向枚举
 * 符合 GB/T 39900-2021 道路交通信号控制系统通用技术要求 A.18.3
 */
/**
 * 上报命令枚举
 */
public enum ReportCommand {
    START("Start", "开始主动上传"),
    STOP("Stop", "停止主动上传");

    private final String code;
    private final String description;

    ReportCommand(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public static ReportCommand fromCode(String code) {
        for (ReportCommand command : values()) {
            if (command.code.equals(code)) {
                return command;
            }
        }
        throw new IllegalArgumentException("Unknown report command code: " + code);
    }
}
