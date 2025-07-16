package com.traffic.gat1049.device.adapter.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConnectionResult {

    /**
     * 连接是否成功
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
     * 连接信息
     */
    private DeviceConnectionInfo connectionInfo;

    /**
     * 连接耗时（毫秒）
     */
    private long connectionTimeMs;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建成功结果
     */
    public static ConnectionResult success(String deviceId, String message) {
        return ConnectionResult.builder()
                .success(true)
                .deviceId(deviceId)
                .message(message)
                .createTime(LocalDateTime.now())
                .build();
    }


    /**
     * 创建失败结果
     */
    public static ConnectionResult failure(String deviceId, String errorCode, String message) {
        return ConnectionResult.builder()
                .success(false)
                .deviceId(deviceId)
                .errorCode(errorCode)
                .message(message)
                .createTime(LocalDateTime.now())
                .build();
    }

    @Data
    @Builder
    public static class SyncDetail {
        private String itemType;
        private String itemId;
        private boolean success;
        private String message;
        private String errorCode;
    }
}
