package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.DetectorPosition;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DetectorPositionAdapter extends XmlAdapter<String, DetectorPosition> {
    @Override
    public DetectorPosition unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return DetectorPosition.fromCode(code);
    }

    @Override
    public String marshal(DetectorPosition detectorPosition) throws Exception {
        if (detectorPosition == null) {
            return null;
        }
        return detectorPosition.getCode();
    }
}
