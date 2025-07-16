package com.traffic.gat1049.device.adapter.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * 适配器事件基类
 */
@Data
public abstract class AdapterEvent extends ApplicationEvent {

    private final String eventId;
    private final String deviceId;
    private final String adapterBrand;
    private final LocalDateTime eventTime;
    private final String eventType;

    public AdapterEvent(Object source, String eventId, String deviceId, String adapterBrand, String eventType) {
        super(source);
        this.eventId = eventId;
        this.deviceId = deviceId;
        this.adapterBrand = adapterBrand;
        this.eventType = eventType;
        this.eventTime = LocalDateTime.now();
    }
}
