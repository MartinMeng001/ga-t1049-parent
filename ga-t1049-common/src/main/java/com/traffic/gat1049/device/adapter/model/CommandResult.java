package com.traffic.gat1049.device.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 命令执行结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandResult {

    /**
     * 执行是否成功
     */
    private boolean success;

    /**
     * 命令ID
     */
    private String commandId;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 结果代码
     */
    private String resultCode;

    /**
     * 结果消息
     */
    private String message;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 执行耗时（毫秒）
     */
    private long executionTimeMs;

    /**
     * 返回数据
     */
    private Map<String, Object> responseData;

    /**
     * 执行时间
     */
    private LocalDateTime executeTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 创建成功结果
     */
    public static CommandResult success(String commandId, String deviceId, String message) {
        return CommandResult.builder()
                .success(true)
                .commandId(commandId)
                .deviceId(deviceId)
                .resultCode("SUCCESS")
                .message(message)
                .completeTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果
     */
    public static CommandResult failure(String commandId, String deviceId, String errorCode, String message) {
        return CommandResult.builder()
                .success(false)
                .commandId(commandId)
                .deviceId(deviceId)
                .errorCode(errorCode)
                .message(message)
                .completeTime(LocalDateTime.now())
                .build();
    }
}
