package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.RouteControlModeAdapter;
import com.traffic.gat1049.model.enums.RouteControlMode;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 干线控制方式
 * 对应文档中的 RouteControlMode
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "RouteCtrlInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteCtrlInfo {//extends BaseState

    /**
     * 线路编号
     */
    @NotBlank(message = "线路编号不能为空")
    @XmlElement(name = "RouteID", required = true)
    @JsonProperty("RouteID")
    private String routeId;

    /**
     * 控制方式
     */
    @XmlElement(name = "CtrlMode", required = true)
    @XmlJavaTypeAdapter(RouteControlModeAdapter.class)
    @JsonProperty("CtrlMode")
    private RouteControlMode value;

    // 构造函数
    public RouteCtrlInfo() {
        //super();
    }

    public RouteCtrlInfo(String routeId, RouteControlMode value) {
        //super();
        this.routeId = routeId;
        this.value = value;
    }

    // Getters and Setters
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public RouteControlMode getValue() {
        return value;
    }

    public void setValue(RouteControlMode value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RouteCtrlInfo{" +
                "routeId='" + routeId + '\'' +
                ", value=" + value +
                "} ";
    }
}
