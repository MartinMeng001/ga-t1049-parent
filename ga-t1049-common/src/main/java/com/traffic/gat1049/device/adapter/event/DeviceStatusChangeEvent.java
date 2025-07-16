package com.traffic.gat1049.device.adapter.event;

import com.traffic.gat1049.device.adapter.model.DeviceStatusData;
import lombok.Getter;

/**
 * 设备状态变化事件
 */
@Getter
public class DeviceStatusChangeEvent extends AdapterEvent {

    private final DeviceStatusData oldStatus;
    private final DeviceStatusData newStatus;
    private final String changeType;

    public DeviceStatusChangeEvent(Object source, String deviceId, String adapterBrand,
                                   DeviceStatusData oldStatus, DeviceStatusData newStatus, String changeType) {
        super(source, java.util.UUID.randomUUID().toString(), deviceId, adapterBrand, "STATUS_CHANGED");
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changeType = changeType;
    }
}
