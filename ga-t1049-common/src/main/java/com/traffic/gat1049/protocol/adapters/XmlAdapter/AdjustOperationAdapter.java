package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.AdjustOperation;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AdjustOperationAdapter extends XmlAdapter<String, AdjustOperation> {
    @Override
    public AdjustOperation unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return AdjustOperation.fromCode(code);
    }

    @Override
    public String marshal(AdjustOperation adjustOperation) throws Exception {
        if (adjustOperation == null) {
            return null;
        }
        return adjustOperation.getCode();
    }
}
