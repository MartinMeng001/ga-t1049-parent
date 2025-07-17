package com.traffic.gat1049.device.adapter.model;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 设备连接信息
 */
@Data
@Builder
public class DeviceConnectionInfo {

    /**
     * 设备ID
     */
    private String deviceId;

    /*
     * ServerIP
     */
    private String ipServer;
    /**
     * 设备IP地址
     */
    private String ipAddress;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 连接类型 (TCP, UDP, SERIAL等)
     */
    private String connectionType;

    /**
     * 是否已连接
     */
    private boolean connected;

    /**
     * 连接建立时间
     */
    private LocalDateTime connectionTime;

    /**
     * 最后通信时间
     */
    private LocalDateTime lastCommunicationTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

    /**
     * 连接超时时间（秒）
     */
    private Integer timeoutSeconds;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;

    /**
     * 连接参数
     */
    private Map<String, Object> connectionParameters;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 检查连接是否过期
     */
    public boolean isExpired(int timeoutSeconds) {
        if (lastCommunicationTime == null) {
            return true;
        }

        LocalDateTime expireTime = lastCommunicationTime.plusSeconds(timeoutSeconds);
        return LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 更新最后通信时间
     */
    public void updateLastCommunicationTime() {
        this.lastCommunicationTime = LocalDateTime.now();
    }

    /**
     * 重置重试计数
     */
    public void resetRetryCount() {
        this.retryCount = 0;
    }

    /**
     * 增加重试计数
     */
    public void incrementRetryCount() {
        if (this.retryCount == null) {
            this.retryCount = 1;
        } else {
            this.retryCount++;
        }
    }

    /**
     * 检查是否达到最大重试次数
     */
    public boolean isMaxRetryReached() {
        return maxRetryCount != null && retryCount != null && retryCount >= maxRetryCount;
    }
}
