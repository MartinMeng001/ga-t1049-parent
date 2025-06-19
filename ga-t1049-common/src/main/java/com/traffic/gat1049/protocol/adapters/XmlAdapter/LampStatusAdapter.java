package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.LampStatus;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LampStatusAdapter extends XmlAdapter<String, LampStatus> {

    @Override
    public LampStatus unmarshal(String code) throws Exception {
        if(code == null || code.trim().isEmpty()){
            return null;
        }
        return LampStatus.valueOf(code);
    }

    @Override
    public String marshal(LampStatus lampStatus) throws Exception {
        if(lampStatus == null){
            return null;
        }
        return lampStatus.getCode();
    }
}
