package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.ControllerErrorType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ControllerErrorTypeAdapter extends XmlAdapter<String, ControllerErrorType> {
    @Override
    public ControllerErrorType unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return ControllerErrorType.fromCode(code);
    }

    @Override
    public String marshal(ControllerErrorType controllerErrorType) throws Exception {
        if (controllerErrorType == null) {
            return null;
        }
        return controllerErrorType.getCode();
    }
}
