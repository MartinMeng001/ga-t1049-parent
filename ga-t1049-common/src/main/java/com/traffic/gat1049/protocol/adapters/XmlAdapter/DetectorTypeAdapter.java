package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.DetectorType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DetectorTypeAdapter extends XmlAdapter<String, DetectorType> {
    @Override
    public DetectorType unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return DetectorType.fromCode(code);
    }

    @Override
    public String marshal(DetectorType detectorType) throws Exception {
        if (detectorType == null) {
            return null;
        }
        return detectorType.getCode();
    }
}
