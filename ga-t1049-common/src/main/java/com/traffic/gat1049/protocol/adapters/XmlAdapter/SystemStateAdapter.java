package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.SystemState;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class SystemStateAdapter extends XmlAdapter<String, SystemState> {
    @Override
    public SystemState unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return SystemState.fromCode(code);
    }

    @Override
    public String marshal(SystemState systemState) throws Exception {
        if (systemState == null) {
            return null;
        }
        return systemState.getCode();
    }
}
