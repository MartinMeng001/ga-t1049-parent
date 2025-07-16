package com.traffic.gat1049.device.management;

import com.traffic.gat1049.device.adapter.model.DeviceInfo;

import java.util.List;

/**
 * 设备发现服务接口
 */
public interface DeviceDiscoveryService {

    /**
     * 启动设备发现
     */
    void startDiscovery();

    /**
     * 停止设备发现
     */
    void stopDiscovery();

    /**
     * 发现设备
     */
    List<DeviceInfo> discoverDevices();

    /**
     * 发现指定网段的设备
     */
    List<DeviceInfo> discoverDevices(String networkSegment);
}
