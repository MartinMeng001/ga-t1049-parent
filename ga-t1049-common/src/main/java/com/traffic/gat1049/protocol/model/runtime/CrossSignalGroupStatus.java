package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 路口信号组灯色状态
 * 对应GA/T 1049.2标准中的 CrossSignalGroupStatus
 * 表B.31 路口信号组灯色状态
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossSignalGroupStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossSignalGroupStatus {

    /**
     * 路口编号
     * 取值同表B.6中路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 开始时间（信号机本地时间）
     * 信号机本地时间，精确到毫秒，格式 YYYY-MM-DD hh:mm:ss.SSS
     */
    @NotBlank(message = "灯态开始时间不能为空")
    @XmlElement(name = "LampStatusTime", required = true)
    @JsonProperty("LampStatusTime")
    private String lampStatusTime;

    /**
     * 信号组灯色状态列表
     * 包含至少1个信号组灯色状态
     */
    @NotEmpty(message = "信号组灯色状态列表不能为空")
    @XmlElementWrapper(name = "SignalGroupStatusList")
    @XmlElement(name = "SignalGroupStatus")
    @JsonProperty("SignalGroupStatusList")
    private List<SignalGroupStatus> signalGroupStatusList = new ArrayList<>();

    // 构造函数
    public CrossSignalGroupStatus() {
    }

    public CrossSignalGroupStatus(String crossId) {
        this.crossId = crossId;
    }

    public CrossSignalGroupStatus(String crossId, String lampStatusTime) {
        this.crossId = crossId;
        this.lampStatusTime = lampStatusTime;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public String getLampStatusTime() {
        return lampStatusTime;
    }

    public void setLampStatusTime(String lampStatusTime) {
        this.lampStatusTime = lampStatusTime;
    }

    public List<SignalGroupStatus> getSignalGroupStatusList() {
        return signalGroupStatusList;
    }

    public void setSignalGroupStatusList(List<SignalGroupStatus> signalGroupStatusList) {
        this.signalGroupStatusList = signalGroupStatusList;
    }

    @Override
    public String toString() {
        return "CrossSignalGroupStatus{" +
                "crossId='" + crossId + '\'' +
                ", lampStatusTime='" + lampStatusTime + '\'' +
                ", signalGroupStatusList=" + signalGroupStatusList +
                '}';
    }
}