package com.traffic.gat1049.device.adapter.model;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 断开连接结果
 */
@Data
@Builder
public class DisconnectionResult {

    /**
     * 断开是否成功
     */
    private boolean success;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 结果消息
     */
    private String message;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 断开耗时（毫秒）
     */
    private long disconnectionTimeMs;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建成功结果
     */
    public static DisconnectionResult success(String deviceId) {
        return DisconnectionResult.builder()
                .success(true)
                .deviceId(deviceId)
                .message("断开连接成功")
                .createTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果
     */
    public static DisconnectionResult failure(String deviceId, String errorCode, String message) {
        return DisconnectionResult.builder()
                .success(false)
                .deviceId(deviceId)
                .errorCode(errorCode)
                .message(message)
                .createTime(LocalDateTime.now())
                .build();
    }
}
