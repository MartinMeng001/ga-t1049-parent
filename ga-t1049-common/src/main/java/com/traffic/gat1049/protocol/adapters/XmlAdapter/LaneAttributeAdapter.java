package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.LaneAttribute;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LaneAttributeAdapter extends XmlAdapter<String, LaneAttribute> {
    @Override
    public LaneAttribute unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return LaneAttribute.fromCode(code);
    }

    @Override
    public String marshal(LaneAttribute laneAttribute) throws Exception {
        if (laneAttribute == null) {
            return null;
        }
        return laneAttribute.getCode();
    }
}
