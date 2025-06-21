package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.FlowType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class FlowTypeAdapter extends XmlAdapter<String, FlowType> {
    @Override
    public FlowType unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return FlowType.fromCode(code);
    }

    @Override
    public String marshal(FlowType flowType) throws Exception {
        if (flowType == null) {
            return null;
        }
        return flowType.getCode();
    }
}
