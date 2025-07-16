package com.traffic.gat1049.device.adapter.model;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 设备信息
 */
@Data
@Builder
public class DeviceInfo {

    private String deviceId;
    private String deviceName;
    private String brand;
    private String model;
    private String ipAddress;
    private Integer port;
    private String protocolVersion;
    private LocalDateTime discoveryTime;
    private boolean online;
    private String description;
}
