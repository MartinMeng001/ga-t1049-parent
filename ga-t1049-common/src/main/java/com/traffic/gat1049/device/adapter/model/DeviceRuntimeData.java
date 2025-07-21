package com.traffic.gat1049.device.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备实时数据
 * 包含信号机的实时运行数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRuntimeData {

    /**
     * 设备ID
     */
    private String controllerId;

    /**
     * 数据时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 交通流数据
     */
    private List<TrafficFlowData> trafficFlowData;

    /**
     * 检测器数据
     */
    private List<DetectorData> detectorData;

    /**
     * 性能统计数据
     */
    private PerformanceStats performanceStats;

    /**
     * 系统资源使用情况
     */
    private SystemResources systemResources;

    /**
     * 扩展运行数据
     */
    private Map<String, Object> extendedData;

    @Data
    @Builder
    public static class TrafficFlowData {
        private String laneId;
        private Integer vehicleCount;
        private Double avgSpeed;
        private Double occupancy;
        private LocalDateTime collectTime;
    }

    @Data
    @Builder
    public static class DetectorData {
        private Integer detectorNo;
        private String detectorType;
        private Integer vehicleCount;
        private Double occupancy;
        private LocalDateTime collectTime;
    }

    @Data
    @Builder
    public static class PerformanceStats {
        private Double cpuUsage;
        private Double memoryUsage;
        private Long networkTraffic;
        private Integer responseTime;
    }

    @Data
    @Builder
    public static class SystemResources {
        private Double cpuUsagePercent;
        private Double memoryUsagePercent;
        private Double diskUsagePercent;
        private Double temperature;
    }
}
