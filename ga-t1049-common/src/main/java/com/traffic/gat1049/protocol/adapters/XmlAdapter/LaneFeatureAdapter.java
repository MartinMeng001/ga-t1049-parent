package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.LaneFeature;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LaneFeatureAdapter extends XmlAdapter<String, LaneFeature> {
    @Override
    public LaneFeature unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return LaneFeature.fromCode(code);
    }

    @Override
    public String marshal(LaneFeature laneFeature) throws Exception {
        if (laneFeature == null) {
            return null;
        }
        return laneFeature.getCode();
    }
}
