package com.traffic.gat1049.protocol.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.ControlModeAdapter;
import com.traffic.gat1049.protocol.model.signal.PlanParam;
import com.traffic.gat1049.model.enums.ControlMode;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 下发中心预案命令
 * 对应文档中的 CenterPlan (B.3.5)
 *
 * 更新说明：
 * 1. 控制方式字段名从 CrossControlMode 修改为 ControlMode
 * 2. 添加了 MaxRunTime（预案最大运行时长）字段
 * 3. 字段验证按照表B.47要求
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CenterPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class CenterPlan {

    /**
     * 控制方式
     * 取值按表B.21要求
     */
    @NotNull(message = "控制方式不能为空")
    @XmlElement(name = "ControlMode", required = true)
    @XmlJavaTypeAdapter(ControlModeAdapter.class)
    @JsonProperty("ControlMode")
    private ControlMode controlMode;

    /**
     * 预案最大运行时长
     * 取值1-1440，单位为分钟(min)
     */
    @NotNull(message = "预案最大运行时长不能为空")
    @Min(value = 1, message = "预案最大运行时长最小值为1分钟")
    @Max(value = 1440, message = "预案最大运行时长最大值为1440分钟")
    @XmlElement(name = "MaxRunTime", required = true)
    @JsonProperty("MaxRunTime")
    private Integer maxRunTime;

    /**
     * 配时方案参数
     * 按表B.16要求
     */
    @NotNull(message = "配时方案参数不能为空")
    @XmlElement(name = "PlanParam", required = true)
    @JsonProperty("PlanParam")
    private PlanParam planParam;

    // 构造函数
    public CenterPlan() {
        super();
    }

    public CenterPlan(ControlMode controlMode, Integer maxRunTime, PlanParam planParam) {
        super();
        this.controlMode = controlMode;
        this.maxRunTime = maxRunTime;
        this.planParam = planParam;
    }

    /**
     * 兼容性构造函数，maxRunTime默认为60分钟
     */
    public CenterPlan(ControlMode controlMode, PlanParam planParam) {
        this(controlMode, 60, planParam);
    }

    // Getters and Setters
    public ControlMode getControlMode() {
        return controlMode;
    }

    public void setControlMode(ControlMode controlMode) {
        this.controlMode = controlMode;
    }

    public Integer getMaxRunTime() {
        return maxRunTime;
    }

    public void setMaxRunTime(Integer maxRunTime) {
        this.maxRunTime = maxRunTime;
    }

    public PlanParam getPlanParam() {
        return planParam;
    }

    public void setPlanParam(PlanParam planParam) {
        this.planParam = planParam;
    }

    // 兼容性方法 - 保持向后兼容
    @Deprecated
    public ControlMode getCrossControlMode() {
        return getControlMode();
    }

    @Deprecated
    public void setCrossControlMode(ControlMode crossControlMode) {
        setControlMode(crossControlMode);
    }

    @Override
    public String toString() {
        return "CenterPlan{" +
                "controlMode=" + controlMode +
                ", maxRunTime=" + maxRunTime +
                ", planParam=" + planParam +
                '}';
    }

    /**
     * 验证字段有效性
     */
    public void validate() {
        if (controlMode == null) {
            throw new IllegalArgumentException("控制方式不能为空");
        }
        if (maxRunTime == null || maxRunTime < 1 || maxRunTime > 1440) {
            throw new IllegalArgumentException("预案最大运行时长必须在1-1440分钟范围内");
        }
        if (planParam == null) {
            throw new IllegalArgumentException("配时方案参数不能为空");
        }
    }
}