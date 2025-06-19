package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.LaneMovement;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LaneMovementAdapter extends XmlAdapter<String, LaneMovement> {
    @Override
    public LaneMovement unmarshal(String code) throws Exception {
        if(code == null || code.trim().isEmpty()){
            return null;
        }
        return LaneMovement.valueOf(code);
    }

    @Override
    public String marshal(LaneMovement laneMovement) throws Exception {
        if(laneMovement == null){
            return null;
        }
        return laneMovement.getCode();
    }
}
