package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.CrossGrade;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CrossGradeAdapter extends XmlAdapter<String, CrossGrade> {
    @Override
    public CrossGrade unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return CrossGrade.fromCode(code);
    }

    @Override
    public String marshal(CrossGrade crossGrade) throws Exception {
        if (crossGrade == null) {
            return null;
        }
        return crossGrade.getCode();
    }
}
