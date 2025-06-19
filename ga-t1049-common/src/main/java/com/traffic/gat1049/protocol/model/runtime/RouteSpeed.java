package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 干线路段推荐车速
 * 对应文档中的 RouteSpeed
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "RouteSpeed")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteSpeed  { //extends BaseState

    /**
     * 线路编号
     */
    @NotBlank(message = "线路编号不能为空")
    @XmlElement(name = "RouteID", required = true)
    @JsonProperty("RouteID")
    private String routeId;

    /**
     * 路段推荐车速列表
     */
    @NotEmpty(message = "路段推荐车速列表不能为空")
    @XmlElementWrapper(name = "RoadSectionSpeedList")
    @XmlElement(name = "RoadSectionSpeed")
    @JsonProperty("RoadSectionSpeedList")
    private List<RoadSectionSpeed> roadSectionSpeedList = new ArrayList<>();

    // 构造函数
    public RouteSpeed() {
        //super();
    }

    public RouteSpeed(String routeId) {
        //super();
        this.routeId = routeId;
    }

    // Getters and Setters
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public List<RoadSectionSpeed> getRoadSectionSpeedList() {
        return roadSectionSpeedList;
    }

    public void setRoadSectionSpeedList(List<RoadSectionSpeed> roadSectionSpeedList) {
        this.roadSectionSpeedList = roadSectionSpeedList;
    }

    @Override
    public String toString() {
        return "RouteSpeed{" +
                "routeId='" + routeId + '\'' +
                ", roadSectionSpeedList=" + roadSectionSpeedList +
                "} " + super.toString();
    }
}
