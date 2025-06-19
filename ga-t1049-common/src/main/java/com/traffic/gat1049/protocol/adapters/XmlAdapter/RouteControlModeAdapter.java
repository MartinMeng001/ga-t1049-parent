package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.RouteControlMode;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RouteControlModeAdapter extends XmlAdapter<String, RouteControlMode> {
    @Override
    public RouteControlMode unmarshal(String code) throws Exception {
        if(code == null || code.trim().isEmpty()){
            return null;
        }
        return RouteControlMode.fromCode(code);
    }

    @Override
    public String marshal(RouteControlMode routeControlMode) throws Exception {
        if(routeControlMode == null){
            return null;
        }
        return routeControlMode.getCode();
    }
}
