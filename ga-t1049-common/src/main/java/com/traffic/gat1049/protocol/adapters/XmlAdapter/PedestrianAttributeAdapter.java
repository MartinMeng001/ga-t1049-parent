package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.PedestrianAttribute;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PedestrianAttributeAdapter extends XmlAdapter<String, PedestrianAttribute> {
    @Override
    public PedestrianAttribute unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return PedestrianAttribute.fromCode(code);
    }

    @Override
    public String marshal(PedestrianAttribute pedestrianAttribute) throws Exception {
        if (pedestrianAttribute == null) {
            return null;
        }
        return pedestrianAttribute.getCode();
    }
}
