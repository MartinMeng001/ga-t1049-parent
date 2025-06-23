package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.AdjustOperation;
import com.traffic.gat1049.model.enums.ReportDataType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ReportDataTypeAdapter extends XmlAdapter<String, ReportDataType> {
    @Override
    public ReportDataType unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return ReportDataType.fromCode(code);
    }

    @Override
    public String marshal(ReportDataType reportDataType) throws Exception {
        if (reportDataType == null) {
            return null;
        }
        return reportDataType.getCode();
    }
}
