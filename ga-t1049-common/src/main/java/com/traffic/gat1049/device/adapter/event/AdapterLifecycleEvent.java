package com.traffic.gat1049.device.adapter.event;

import com.traffic.gat1049.device.adapter.model.AdapterStatus;
import lombok.Getter;

/**
 * 适配器生命周期事件
 */
@Getter
public class AdapterLifecycleEvent extends AdapterEvent {

    private final AdapterStatus oldStatus;
    private final AdapterStatus newStatus;
    private final String operation;

    public AdapterLifecycleEvent(Object source, String adapterBrand,
                                 AdapterStatus oldStatus, AdapterStatus newStatus, String operation) {
        super(source, java.util.UUID.randomUUID().toString(), null, adapterBrand, "ADAPTER_LIFECYCLE");
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.operation = operation;
    }
}
