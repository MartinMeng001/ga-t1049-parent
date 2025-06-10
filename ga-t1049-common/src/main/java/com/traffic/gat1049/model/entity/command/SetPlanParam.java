package com.traffic.gat1049.model.entity.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.entity.base.BaseCommand;
import com.traffic.gat1049.model.entity.signal.PlanParam;
import com.traffic.gat1049.model.enums.OperationType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 设置配时方案参数命令
 * 对应文档中的 SetPlanParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SetPlanParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class SetPlanParam extends BaseCommand {

    /**
     * 设置类型（新增，修改，删除）
     */
    @XmlElement(name = "Oper", required = true)
    @JsonProperty("Oper")
    private OperationType oper;

    /**
     * 配时方案参数
     */
    @XmlElement(name = "PlanParam", required = true)
    @JsonProperty("PlanParam")
    private PlanParam planParam;

    // 构造函数
    public SetPlanParam() {
        super();
    }

    public SetPlanParam(OperationType oper, PlanParam planParam) {
        super();
        this.oper = oper;
        this.planParam = planParam;
    }

    // Getters and Setters
    public OperationType getOper() {
        return oper;
    }

    public void setOper(OperationType oper) {
        this.oper = oper;
    }

    public PlanParam getPlanParam() {
        return planParam;
    }

    public void setPlanParam(PlanParam planParam) {
        this.planParam = planParam;
    }

    @Override
    public String toString() {
        return "SetPlanParam{" +
                "oper=" + oper +
                ", planParam=" + planParam +
                "} " + super.toString();
    }
}
