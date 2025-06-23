package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.ScheduleType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ScheduleTypeAdapter extends XmlAdapter<String, ScheduleType> {

    @Override
    public ScheduleType unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return ScheduleType.fromCode(code);
    }

    @Override
    public String marshal(ScheduleType scheduleType) throws Exception {
        if (scheduleType == null) {
            return null;
        }
        return scheduleType.getCode();
    }
}
