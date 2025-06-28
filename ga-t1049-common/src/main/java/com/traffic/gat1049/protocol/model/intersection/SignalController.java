package com.traffic.gat1049.protocol.model.intersection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.enums.CommMode;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.CommModeAdapter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * 信号机参数
 * 对应GA/T 1049.2-2016标准中的SignalController
 * 更新至最新标准定义(5.1.8节)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SignalController")
@XmlAccessorType(XmlAccessType.FIELD)
public class SignalController {

    /**
     * 信号机设备编号
     * 取值：交通管理部门机构代码（按GA/T 380-2012前6位）+99+4位数字
     */
    @NotBlank(message = "信号机设备编号不能为空")
    @Pattern(regexp = "\\d{6}99\\d{4}", message = "信号机设备编号格式错误，应为6位机构代码+99+4位数字")
    @XmlElement(name = "SignalControllerID", required = true)
    @JsonProperty("SignalControllerID")
    private String signalControllerID;

    /**
     * 供应商
     * 符合GA/T 543.6的DE00655
     */
    @NotBlank(message = "供应商不能为空")
    @XmlElement(name = "Supplier", required = true)
    @JsonProperty("Supplier")
    private String supplier;

    /**
     * 规格型号
     * 长度16，取值应符合GB 25280-2016的4.2
     */
    @NotBlank(message = "规格型号不能为空")
    @Pattern(regexp = ".{1,16}", message = "规格型号长度应为1-16位")
    @XmlElement(name = "Type", required = true)
    @JsonProperty("Type")
    private String type;

    /**
     * 识别码
     * 长度16，取值应符合GB 25280-2016的4.3
     */
    @NotBlank(message = "识别码不能为空")
    @Pattern(regexp = ".{1,16}", message = "识别码长度应为1-16位")
    @XmlElement(name = "ID", required = true)
    @JsonProperty("ID")
    private String id;

    /**
     * 通信接口
     * 取值：10:以太网, 11:TCP Client, 12:TCP Server, 13:UDP, 20:串口, 99:其他
     */
    @XmlElement(name = "CommMode", required = true)
    @XmlJavaTypeAdapter(CommModeAdapter.class)
    @JsonProperty("CommMode")
    private CommMode commMode;

    /**
     * 信号机通信IP地址
     * 符合GA/T 543.6的DE00650，可空
     */
    @XmlElement(name = "IP", nillable = true)
    @JsonProperty("IP")
    private String ip;

    /**
     * 子网掩码
     * 可空
     */
    @XmlElement(name = "SubMask", nillable = true)
    @JsonProperty("SubMask")
    private String subMask;

    /**
     * 网关
     * 可空
     */
    @XmlElement(name = "Gateway", nillable = true)
    @JsonProperty("Gateway")
    private String gateway;

    /**
     * 端口号
     * 取值0-65534
     */
    @Min(value = 0, message = "端口号不能小于0")
    @Max(value = 65534, message = "端口号不能大于65534")
    @XmlElement(name = "Port", required = true)
    @JsonProperty("Port")
    private Integer port;

    /**
     * 是否有柜门状态检测
     * 取值：1:是, 0:否
     */
    @Min(value = 0, message = "柜门状态检测标志只能为0或1")
    @Max(value = 1, message = "柜门状态检测标志只能为0或1")
    @XmlElement(name = "HasDoorStatus", required = true)
    @JsonProperty("HasDoorStatus")
    private Integer hasDoorStatus;

    /**
     * 安装位置经度
     * 使用WGS84坐标系，符合GA/T 543.9的DE01119
     */
    @XmlElement(name = "Longitude", required = true)
    @JsonProperty("Longitude")
    private Double longitude;

    /**
     * 安装位置纬度
     * 使用WGS84坐标系，符合GA/T 543.9的DE01120
     */
    @XmlElement(name = "Latitude", required = true)
    @JsonProperty("Latitude")
    private Double latitude;

    /**
     * 信号机控制路口编号列表
     * 包含至少1个路口编号<CrossID>，主路口排列在列表中首位
     */
    @NotEmpty(message = "控制路口编号列表不能为空")
    @XmlElementWrapper(name = "CrossIDList")
    @XmlElement(name = "CrossID")
    @JsonProperty("CrossIDList")
    private List<String> crossIDList = new ArrayList<>();

    // 构造函数
    public SignalController() {
    }

    public SignalController(String signalControllerID, String supplier, String type, String id) {
        this.signalControllerID = signalControllerID;
        this.supplier = supplier;
        this.type = type;
        this.id = id;
    }

    // Getters and Setters
    public String getSignalControllerID() {
        return signalControllerID;
    }

    public void setSignalControllerID(String signalControllerID) {
        this.signalControllerID = signalControllerID;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CommMode getCommMode() {
        return commMode;
    }

    public void setCommMode(CommMode commMode) {
        this.commMode = commMode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSubMask() {
        return subMask;
    }

    public void setSubMask(String subMask) {
        this.subMask = subMask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getHasDoorStatus() {
        return hasDoorStatus;
    }

    public void setHasDoorStatus(Integer hasDoorStatus) {
        this.hasDoorStatus = hasDoorStatus;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public List<String> getCrossIDList() {
        return crossIDList;
    }

    public void setCrossIDList(List<String> crossIDList) {
        this.crossIDList = crossIDList;
    }

    @Override
    public String toString() {
        return "SignalController{" +
                "signalControllerID='" + signalControllerID + '\'' +
                ", supplier='" + supplier + '\'' +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", commMode=" + commMode +
                ", ip='" + ip + '\'' +
                ", subMask='" + subMask + '\'' +
                ", gateway='" + gateway + '\'' +
                ", port=" + port +
                ", hasDoorStatus=" + hasDoorStatus +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", crossIDList=" + crossIDList +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SignalController that = (SignalController) obj;
        return signalControllerID != null ? signalControllerID.equals(that.signalControllerID) : that.signalControllerID == null;
    }

    @Override
    public int hashCode() {
        return signalControllerID != null ? signalControllerID.hashCode() : 0;
    }
}