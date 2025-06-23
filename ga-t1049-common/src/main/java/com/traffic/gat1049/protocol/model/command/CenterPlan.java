package com.traffic.gat1049.protocol.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.ControlModeAdapter;
import com.traffic.gat1049.protocol.model.base.BaseCommand;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.model.enums.ControlMode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 下发中心预案命令
 * 对应文档中的 CenterPlan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CenterPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class CenterPlan {//extends BaseCommand

    /**
     * 控制方式
     */
    @XmlElement(name = "CrossControlMode", required = true)
    @XmlJavaTypeAdapter(ControlModeAdapter.class)
    @JsonProperty("CrossControlMode")
    private ControlMode crossControlMode;

    /**
     * 配时方案参数
     */
    @XmlElement(name = "PlanParam", required = true)
    @JsonProperty("PlanParam")
    private PlanParam planParam;

    // 构造函数
    public CenterPlan() {
        super();
    }

    public CenterPlan(ControlMode crossControlMode, PlanParam planParam) {
        super();
        this.crossControlMode = crossControlMode;
        this.planParam = planParam;
    }

    // Getters and Setters
    public ControlMode getCrossControlMode() {
        return crossControlMode;
    }

    public void setCrossControlMode(ControlMode crossControlMode) {
        this.crossControlMode = crossControlMode;
    }

    public PlanParam getPlanParam() {
        return planParam;
    }

    public void setPlanParam(PlanParam planParam) {
        this.planParam = planParam;
    }

    @Override
    public String toString() {
        return "CenterPlan{" +
                "crossControlMode=" + crossControlMode +
                ", planParam=" + planParam +
                "} " + super.toString();
    }
}
