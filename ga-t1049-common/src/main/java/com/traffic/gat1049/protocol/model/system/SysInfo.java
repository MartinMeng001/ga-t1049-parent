package com.traffic.gat1049.protocol.model.system;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统参数
 * 对应文档中的 SysInfo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SysInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class SysInfo {//extends BaseParam

    /**
     * 系统名称
     */
    @NotBlank(message = "系统名称不能为空")
    @XmlElement(name = "SysName", required = true)
    @JsonProperty("SysName")
    private String sysName;

    /**
     * 版本号
     */
    @NotBlank(message = "版本号不能为空")
    @XmlElement(name = "SysVersion", required = true)
    @JsonProperty("SysVersion")
    private String sysVersion;

    /**
     * 供应商
     */
    @NotBlank(message = "供应商不能为空")
    @XmlElement(name = "Supplier", required = true)
    @JsonProperty("Supplier")
    private String supplier;

    /**
     * 路口编号列表
     */
    @NotEmpty(message = "路口编号列表不能为空")
    @XmlElementWrapper(name = "CrossIDList")
    @XmlElement(name = "CrossID")
    @JsonProperty("CrossIDList")
    private List<String> crossIdList = new ArrayList<>();

    /**
     * 线路编号列表
     */
    @XmlElementWrapper(name = "RouteIDList")
    @XmlElement(name = "RouteID")
    @JsonProperty("RouteIDList")
    private List<String> routeIdList = new ArrayList<>();

    /**
     * 区域编号列表
     */
    @XmlElementWrapper(name = "RegionIDList")
    @XmlElement(name = "RegionID")
    @JsonProperty("RegionIDList")
    private List<String> regionIdList = new ArrayList<>();

    /**
     * 子区编号列表
     */
    @XmlElementWrapper(name = "SubRegionIDList")
    @XmlElement(name = "SubRegionID")
    @JsonProperty("SubRegionIDList")
    private List<String> subRegionIdList = new ArrayList<>();

    /**
     * 信号机编号列表
     */
    @NotEmpty(message = "信号机编号列表不能为空")
    @XmlElementWrapper(name = "SignalControllerIDList")
    @XmlElement(name = "SignalControllerID")
    @JsonProperty("SignalControllerIDList")
    private List<String> signalControllerIdList = new ArrayList<>();

    // 构造函数
    public SysInfo() {
        //super();
    }

    public SysInfo(String sysName, String sysVersion, String supplier) {
        //super();
        this.sysName = sysName;
        this.sysVersion = sysVersion;
        this.supplier = supplier;
    }

    // Getters and Setters
    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getSysVersion() {
        return sysVersion;
    }

    public void setSysVersion(String sysVersion) {
        this.sysVersion = sysVersion;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public List<String> getCrossIdList() {
        return crossIdList;
    }

    public void setCrossIdList(List<String> crossIdList) {
        this.crossIdList = crossIdList;
    }

    public List<String> getRouteIdList() {
        return routeIdList;
    }

    public void setRouteIdList(List<String> routeIdList) {
        this.routeIdList = routeIdList;
    }

    public List<String> getRegionIdList() {
        return regionIdList;
    }

    public void setRegionIdList(List<String> regionIdList) {
        this.regionIdList = regionIdList;
    }

    public List<String> getSubRegionIdList() {
        return subRegionIdList;
    }

    public void setSubRegionIdList(List<String> subRegionIdList) {
        this.subRegionIdList = subRegionIdList;
    }

    public List<String> getSignalControllerIdList() {
        return signalControllerIdList;
    }

    public void setSignalControllerIdList(List<String> signalControllerIdList) {
        this.signalControllerIdList = signalControllerIdList;
    }

    @Override
    public String toString() {
        return "SysInfo{" +
                "sysName='" + sysName + '\'' +
                ", sysVersion='" + sysVersion + '\'' +
                ", supplier='" + supplier + '\'' +
                ", crossIdList=" + crossIdList +
                ", routeIdList=" + routeIdList +
                ", regionIdList=" + regionIdList +
                ", signalControllerIdList=" + signalControllerIdList +
                "} " + super.toString();
    }
}
