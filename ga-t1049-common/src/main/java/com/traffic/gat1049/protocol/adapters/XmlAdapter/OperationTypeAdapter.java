package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.OperationType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OperationTypeAdapter extends XmlAdapter<String, OperationType> {
    @Override
    public OperationType unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return OperationType.fromCode(code);
    }

    @Override
    public String marshal(OperationType operationType) throws Exception {
        if (operationType == null) {
            return null;
        }
        return operationType.getCode();
    }
}
