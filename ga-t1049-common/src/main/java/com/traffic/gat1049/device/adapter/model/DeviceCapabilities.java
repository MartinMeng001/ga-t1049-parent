package com.traffic.gat1049.device.adapter.model;

import lombok.Data;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * 设备能力信息
 * 描述设备支持的功能和特性
 */
@Data
@Builder
public class DeviceCapabilities {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 固件版本
     */
    private String firmwareVersion;

    /**
     * 硬件版本
     */
    private String hardwareVersion;

    /**
     * 支持的协议版本
     */
    private List<String> supportedProtocols;

    /**
     * 最大信号组数
     */
    private Integer maxSignalGroups;

    /**
     * 最大阶段数
     */
    private Integer maxStages;

    /**
     * 最大配时方案数
     */
    private Integer maxPlans;

    /**
     * 最大检测器数
     */
    private Integer maxDetectors;

    /**
     * 支持的控制模式
     */
    private List<String> supportedControlModes;

    /**
     * 支持的通信接口
     */
    private List<String> supportedInterfaces;

    /**
     * 扩展能力
     */
    private Map<String, Object> extendedCapabilities;

    /**
     * 检查是否支持指定功能
     */
    public boolean supportsFeature(String feature) {
        return extendedCapabilities != null &&
                extendedCapabilities.containsKey(feature) &&
                Boolean.TRUE.equals(extendedCapabilities.get(feature));
    }
}
