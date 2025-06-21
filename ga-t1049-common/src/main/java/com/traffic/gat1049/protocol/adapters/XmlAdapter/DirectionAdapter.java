package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.Direction;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DirectionAdapter extends XmlAdapter<String, Direction> {
    @Override
    public Direction unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return Direction.fromCode(code);
    }

    @Override
    public String marshal(Direction direction) throws Exception {
        if (direction == null) {
            return null;
        }
        return direction.getCode();
    }
}
