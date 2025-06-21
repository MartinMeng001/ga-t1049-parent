package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.CrossFeature;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CrossFeatureAdapter extends XmlAdapter<String, CrossFeature> {
    @Override
    public CrossFeature unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return CrossFeature.fromCode(code);
    }

    @Override
    public String marshal(CrossFeature crossFeature) throws Exception {
        if (crossFeature == null) {
            return null;
        }
        return crossFeature.getCode();
    }
}
