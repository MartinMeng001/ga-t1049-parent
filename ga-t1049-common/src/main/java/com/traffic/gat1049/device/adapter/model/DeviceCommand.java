package com.traffic.gat1049.device.adapter.model;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 设备命令
 * 用于发送给设备的控制命令
 */
@Data
@Builder
public class DeviceCommand {

    /**
     * 命令ID
     */
    private String commandId;

    /**
     * 目标设备ID
     */
    private String targetDeviceId;

    /**
     * 命令类型
     */
    private CommandType commandType;

    /**
     * 命令代码
     */
    private String commandCode;

    /**
     * 命令参数
     */
    private Map<String, Object> parameters;

    /**
     * 命令优先级
     * 1-低, 2-中, 3-高, 4-紧急
     */
    private Integer priority;

    /**
     * 超时时间（秒）
     */
    private Integer timeoutSeconds;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 执行时间
     */
    private LocalDateTime executeTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 命令来源
     */
    private String source;

    /**
     * 命令描述
     */
    private String description;

    /**
     * 检查命令是否过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 检查命令是否为紧急命令
     */
    public boolean isUrgent() {
        return priority != null && priority >= 4;
    }

    /**
     * 命令类型枚举
     */
    public enum CommandType {
        CONFIG_UPDATE("配置更新"),
        PLAN_SWITCH("方案切换"),
        STAGE_SWITCH("阶段切换"),
        SYSTEM_CONTROL("系统控制"),
        FAULT_RESET("故障复位"),
        TIME_SYNC("时间同步"),
        RESTART("重启"),
        SHUTDOWN("关机"),
        CUSTOM("自定义");

        private final String displayName;

        CommandType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
