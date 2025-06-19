package com.traffic.gat1049.protocol.adapters.XmlAdapter;

import com.traffic.gat1049.model.enums.ControlMode;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * ControlMode XML 适配器
 * 用于 XML 序列化时使用 code 而不是枚举名称
 */
public class ControlModeAdapter extends XmlAdapter<String, ControlMode> {

    @Override
    public ControlMode unmarshal(String code) throws Exception {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        return ControlMode.fromCode(code);
    }

    @Override
    public String marshal(ControlMode controlMode) throws Exception {
        if (controlMode == null) {
            return null;
        }
        return controlMode.getCode();
    }
}
