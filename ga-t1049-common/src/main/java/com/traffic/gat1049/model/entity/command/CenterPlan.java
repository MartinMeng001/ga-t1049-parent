package com.traffic.gat1049.model.entity.command;

/**
 * 下发中心预案命令
 * 对应文档中的 CenterPlan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CenterPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class CenterPlan extends BaseCommand {

    /**
     * 控制方式
     */
    @XmlElement(name = "CrossControlMode", required = true)
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
