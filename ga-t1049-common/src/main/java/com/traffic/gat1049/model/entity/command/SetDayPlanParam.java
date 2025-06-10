package com.traffic.gat1049.model.entity.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.entity.base.BaseCommand;
import com.traffic.gat1049.model.entity.signal.DayPlanParam;
import com.traffic.gat1049.model.enums.OperationType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 设置日计划参数命令
 * 对应文档中的 SetDayPlanParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SetDayPlanParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class SetDayPlanParam extends BaseCommand {

    /**
     * 设置类型（新增，修改，删除）
     */
    @XmlElement(name = "Oper", required = true)
    @JsonProperty("Oper")
    private OperationType oper;

    /**
     * 日计划参数
     */
    @XmlElement(name = "DayPlanParam", required = true)
    @JsonProperty("DayPlanParam")
    private DayPlanParam dayPlanParam;

    // 构造函数
    public SetDayPlanParam() {
        super();
    }

    public SetDayPlanParam(OperationType oper, DayPlanParam dayPlanParam) {
        super();
        this.oper = oper;
        this.dayPlanParam = dayPlanParam;
    }

    // Getters and Setters
    public OperationType getOper() {
        return oper;
    }

    public void setOper(OperationType oper) {
        this.oper = oper;
    }

    public DayPlanParam getDayPlanParam() {
        return dayPlanParam;
    }

    public void setDayPlanParam(DayPlanParam dayPlanParam) {
        this.dayPlanParam = dayPlanParam;
    }

    @Override
    public String toString() {
        return "SetDayPlanParam{" +
                "oper=" + oper +
                ", dayPlanParam=" + dayPlanParam +
                "} " + super.toString();
    }
}
