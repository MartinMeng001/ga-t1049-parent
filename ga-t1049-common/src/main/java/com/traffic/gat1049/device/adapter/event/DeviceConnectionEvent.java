package com.traffic.gat1049.device.adapter.event;

import com.traffic.gat1049.device.adapter.model.DeviceConnectionInfo;
import lombok.Getter;

/**
 * 设备连接事件
 */
@Getter
public class DeviceConnectionEvent extends AdapterEvent {

    private final boolean connected;
    private final DeviceConnectionInfo connectionInfo;
    private final String reason;

    public DeviceConnectionEvent(Object source, String deviceId, String adapterBrand,
                                 boolean connected, DeviceConnectionInfo connectionInfo, String reason) {
        super(source, java.util.UUID.randomUUID().toString(), deviceId, adapterBrand,
                connected ? "DEVICE_CONNECTED" : "DEVICE_DISCONNECTED");
        this.connected = connected;
        this.connectionInfo = connectionInfo;
        this.reason = reason;
    }

    public static DeviceConnectionEvent connected(Object source, String deviceId, String adapterBrand,
                                                  DeviceConnectionInfo connectionInfo) {
        return new DeviceConnectionEvent(source, deviceId, adapterBrand, true, connectionInfo, "连接成功");
    }

    public static DeviceConnectionEvent disconnected(Object source, String deviceId, String adapterBrand,
                                                     String reason) {
        return new DeviceConnectionEvent(source, deviceId, adapterBrand, false, null, reason);
    }
}
