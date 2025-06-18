package com.traffic.gat1049.protocol.model.system;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseParam;
import com.traffic.gat1049.model.enums.RouteType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 线路参数
 * 对应文档中的 RouteParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "RouteParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteParam {//extends BaseParam

    /**
     * 线路编号 - 全局唯一，取值6位行政区划代码+3位数字
     */
    @NotBlank(message = "线路编号不能为空")
    @Pattern(regexp = "\\d{9}", message = "线路编号格式错误，应为9位数字")
    @XmlElement(name = "RouteID", required = true)
    @JsonProperty("RouteID")
    private String routeId;

    /**
     * 线路名称
     */
    @NotBlank(message = "线路名称不能为空")
    @XmlElement(name = "RouteName", required = true)
    @JsonProperty("RouteName")
    private String routeName;

    /**
     * 线路类型
     */
    @XmlElement(name = "Type", required = true)
    @JsonProperty("Type")
    private RouteType type;

    /**
     * 线路包含路口列表
     */
    @NotEmpty(message = "线路路口列表不能为空")
    @XmlElementWrapper(name = "RouteCrossList")
    @XmlElement(name = "RouteCross")
    @JsonProperty("RouteCrossList")
    private List<RouteCross> routeCrossList = new ArrayList<>();

    /**
     * 子区编号列表
     */
    @XmlElementWrapper(name = "SubRegionIDList")
    @XmlElement(name = "SubRegionID")
    @JsonProperty("SubRegionIDList")
    private List<String> subRegionIdList = new ArrayList<>();

    // 构造函数
    public RouteParam() {
        super();
    }

    public RouteParam(String routeId, String routeName, RouteType type) {
        super();
        this.routeId = routeId;
        this.routeName = routeName;
        this.type = type;
    }

    // Getters and Setters
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public List<RouteCross> getRouteCrossList() {
        return routeCrossList;
    }

    public void setRouteCrossList(List<RouteCross> routeCrossList) {
        this.routeCrossList = routeCrossList;
    }

    public List<String> getSubRegionIdList() {
        return subRegionIdList;
    }

    public void setSubRegionIdList(List<String> subRegionIdList) {
        this.subRegionIdList = subRegionIdList;
    }

    @Override
    public String toString() {
        return "RouteParam{" +
                "routeId='" + routeId + '\'' +
                ", routeName='" + routeName + '\'' +
                ", type=" + type +
                ", routeCrossList=" + routeCrossList +
                ", subRegionIdList=" + subRegionIdList +
                "} " + super.toString();
    }
}
