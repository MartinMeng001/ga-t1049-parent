package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;
import com.traffic.gat1049.model.enums.ControlMode;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 路口控制方式方案
 * 对应文档中的 CrossModePlan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossModePlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossModePlan extends BaseState {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 控制方式
     */
    @XmlElement(name = "ControlMode", required = true)
    @JsonProperty("ControlMode")
    private ControlMode controlMode;

    /**
     * 控制方案号
     */
    @XmlElement(name = "PlanNo", required = true)
    @JsonProperty("PlanNo")
    private Integer planNo;

    // 构造函数
    public CrossModePlan() {
        super();
    }

    public CrossModePlan(String crossId, ControlMode controlMode, Integer planNo) {
        super();
        this.crossId = crossId;
        this.controlMode = controlMode;
        this.planNo = planNo;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public ControlMode getControlMode() {
        return controlMode;
    }

    public void setControlMode(ControlMode controlMode) {
        this.controlMode = controlMode;
    }

    public Integer getPlanNo() {
        return planNo;
    }

    public void setPlanNo(Integer planNo) {
        this.planNo = planNo;
    }

    @Override
    public String toString() {
        return "CrossModePlan{" +
                "crossId='" + crossId + '\'' +
                ", controlMode=" + controlMode +
                ", planNo=" + planNo +
                "} " + super.toString();
    }
}
