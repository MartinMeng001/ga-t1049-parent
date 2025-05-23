package com.traffic.gat1049.model.entity.command;

/**
 * 设置调度参数命令
 * 对应文档中的 SetScheduleParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SetScheduleParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class SetScheduleParam extends BaseCommand {

    /**
     * 设置类型（新增，修改，删除）
     */
    @XmlElement(name = "Oper", required = true)
    @JsonProperty("Oper")
    private OperationType oper;

    /**
     * 调度参数
     */
    @XmlElement(name = "ScheduleParam", required = true)
    @JsonProperty("ScheduleParam")
    private ScheduleParam scheduleParam;

    // 构造函数
    public SetScheduleParam() {
        super();
    }

    public SetScheduleParam(OperationType oper, ScheduleParam scheduleParam) {
        super();
        this.oper = oper;
        this.scheduleParam = scheduleParam;
    }

    // Getters and Setters
    public OperationType getOper() {
        return oper;
    }

    public void setOper(OperationType oper) {
        this.oper = oper;
    }

    public ScheduleParam getScheduleParam() {
        return scheduleParam;
    }

    public void setScheduleParam(ScheduleParam scheduleParam) {
        this.scheduleParam = scheduleParam;
    }

    @Override
    public String toString() {
        return "SetScheduleParam{" +
                "oper=" + oper +
                ", scheduleParam=" + scheduleParam +
                "} " + super.toString();
    }
}
