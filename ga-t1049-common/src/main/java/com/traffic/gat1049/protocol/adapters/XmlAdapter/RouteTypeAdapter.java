package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.RouteType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RouteTypeAdapter extends XmlAdapter<String, RouteType> {
    @Override
    public RouteType unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return RouteType.fromCode(code);
    }

    @Override
    public String marshal(RouteType routeType) throws Exception {
        if (routeType == null) {
            return null;
        }
        return routeType.getCode();
    }
}
