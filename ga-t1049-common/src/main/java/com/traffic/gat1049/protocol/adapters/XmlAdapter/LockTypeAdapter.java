package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.ControlMode;
import com.traffic.gat1049.model.enums.LockType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LockTypeAdapter extends XmlAdapter<String, LockType> {
    @Override
    public LockType unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return LockType.fromCode(code);
    }

    @Override
    public String marshal(LockType lockType) throws Exception {
        if (lockType == null) {
            return null;
        }
        return lockType.getCode();
    }
}
