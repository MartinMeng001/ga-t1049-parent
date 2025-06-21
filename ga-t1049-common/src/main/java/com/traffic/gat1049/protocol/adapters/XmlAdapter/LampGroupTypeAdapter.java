package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.LampGroupType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LampGroupTypeAdapter extends XmlAdapter<String, LampGroupType> {
    @Override
    public LampGroupType unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return LampGroupType.fromCode(code);
    }

    @Override
    public String marshal(LampGroupType lampGroupType) throws Exception {
        if (lampGroupType == null) {
            return null;
        }
        return lampGroupType.getCode();
    }
}
