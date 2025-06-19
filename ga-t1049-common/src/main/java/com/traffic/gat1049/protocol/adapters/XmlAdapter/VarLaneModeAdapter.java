package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.VarLaneMode;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class VarLaneModeAdapter extends XmlAdapter<String, VarLaneMode> {
    @Override
    public VarLaneMode unmarshal(String code) throws Exception {
        if(code == null || code.trim().isEmpty()){
            return null;
        }
        return VarLaneMode.valueOf(code);
    }

    @Override
    public String marshal(VarLaneMode varLaneMode) throws Exception {
        if(varLaneMode == null){
            return null;
        }
        return varLaneMode.getCode();
    }
}
