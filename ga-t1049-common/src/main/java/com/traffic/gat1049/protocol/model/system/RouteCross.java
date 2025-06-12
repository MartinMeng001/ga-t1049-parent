package com.traffic.gat1049.protocol.model.system;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 线路路口
 * 线路中包含的路口信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteCross {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 与线路中上游路口的距离（米）
     * 线路中的第一个路口设置为0
     */
    @XmlElement(name = "Distance", required = true)
    @JsonProperty("Distance")
    private Integer distance;

    // 构造函数
    public RouteCross() {}

    public RouteCross(String crossId, Integer distance) {
        this.crossId = crossId;
        this.distance = distance;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "RouteCross{" +
                "crossId='" + crossId + '\'' +
                ", distance=" + distance +
                '}';
    }
}
