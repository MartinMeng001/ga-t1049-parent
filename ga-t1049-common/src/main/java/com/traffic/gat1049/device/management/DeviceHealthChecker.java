package com.traffic.gat1049.device.management;

import com.traffic.gat1049.device.adapter.model.HealthCheckReport;

import java.util.List;
import java.util.Map;

/**
 * 设备健康检查器接口
 */
public interface DeviceHealthChecker {

    /**
     * 检查单个设备健康状态
     */
    boolean checkDeviceHealth(String controllerId);

    /**
     * 批量检查设备健康状态
     */
    Map<String, Boolean> batchCheckHealth(List<String> controllerIds);

    /**
     * 获取健康检查报告
     */
    HealthCheckReport getHealthReport();
}
