package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.InterventionType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class InterventionTypeAdapter extends XmlAdapter<String, InterventionType> {
    @Override
    public InterventionType unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return InterventionType.fromCode(code);
    }

    @Override
    public String marshal(InterventionType interventionType) throws Exception {
        if (interventionType == null) {
            return null;
        }
        return interventionType.getCode();
    }
}
