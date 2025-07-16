package com.traffic.gat1049.device.adapter.model;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 健康检查报告
 */
@Data
@Builder
public class HealthCheckReport {

    private Map<String, Boolean> deviceHealthStatus;
    private int totalDevices;
    private int healthyDevices;
    private int unhealthyDevices;
    private LocalDateTime checkTime;
    private long checkDurationMs;

    /**
     * 获取健康率
     */
    public double getHealthRate() {
        return totalDevices > 0 ? (double) healthyDevices / totalDevices * 100 : 0;
    }
}
