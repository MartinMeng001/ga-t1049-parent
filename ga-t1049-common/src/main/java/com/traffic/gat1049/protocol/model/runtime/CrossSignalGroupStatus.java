package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;
import com.traffic.gat1049.protocol.model.signal.SignalGroupStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 路口信号组灯态
 * 对应文档中的 CrossSignalGroupStatus
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossSignalGroupStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossSignalGroupStatus extends BaseState {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 信号组灯态列表
     */
    @NotEmpty(message = "信号组灯态列表不能为空")
    @XmlElementWrapper(name = "SignalGroupStatusList")
    @XmlElement(name = "SignalGroupStatus")
    @JsonProperty("SignalGroupStatusList")
    private List<SignalGroupStatus> signalGroupStatusList = new ArrayList<>();

    // 构造函数
    public CrossSignalGroupStatus() {
        super();
    }

    public CrossSignalGroupStatus(String crossId) {
        super();
        this.crossId = crossId;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
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
                ", signalGroupStatusList=" + signalGroupStatusList +
                "} " + super.toString();
    }
}
