package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.DoorStatus;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 机柜门状态XML适配器
 * 用于在XML序列化/反序列化时处理DoorStatus枚举
 */
public class DoorStatusAdapter extends XmlAdapter<String, DoorStatus> {

    @Override
    public DoorStatus unmarshal(String value) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return DoorStatus.fromCode(value.trim());
    }

    @Override
    public String marshal(DoorStatus doorStatus) throws Exception {
        if (doorStatus == null) {
            return null;
        }
        return doorStatus.getCode();
    }
}