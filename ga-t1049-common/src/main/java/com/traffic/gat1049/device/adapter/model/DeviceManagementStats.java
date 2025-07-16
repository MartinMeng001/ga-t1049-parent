package com.traffic.gat1049.device.adapter.model;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 设备管理统计信息
 */
@Data
@Builder
public class DeviceManagementStats {

    private int totalDevices;
    private int connectedDevices;
    private int disconnectedDevices;
    private int healthyDevices;
    private int unhealthyDevices;
    private int adapterCount;
    private LocalDateTime lastUpdateTime;

    /**
     * 获取连接率
     */
    public double getConnectionRate() {
        return totalDevices > 0 ? (double) connectedDevices / totalDevices * 100 : 0;
    }

    /**
     * 获取健康率
     */
    public double getHealthRate() {
        return connectedDevices > 0 ? (double) healthyDevices / connectedDevices * 100 : 0;
    }
}
