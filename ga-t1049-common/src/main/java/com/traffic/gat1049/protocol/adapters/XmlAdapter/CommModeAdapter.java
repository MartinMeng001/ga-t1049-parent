package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.CommMode;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CommModeAdapter extends XmlAdapter<String, CommMode> {
    @Override
    public CommMode unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return CommMode.fromCode(code);
    }

    @Override
    public String marshal(CommMode commMode) throws Exception {
        if (commMode == null) {
            return null;
        }
        return commMode.getCode();
    }
}
