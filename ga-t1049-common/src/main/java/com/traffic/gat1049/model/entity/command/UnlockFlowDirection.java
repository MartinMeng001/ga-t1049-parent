package com.traffic.gat1049.model.entity.command;

/**
 * 解锁交通流向命令
 * 对应文档中的 UnlockFlowDirection
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "UnlockFlowDirection")
@XmlAccessorType(XmlAccessType.FIELD)
public class UnlockFlowDirection extends BaseCommand {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 交通流类型
     */
    @XmlElement(name = "Type", required = true)
    @JsonProperty("Type")
    private FlowType type;

    /**
     * 进口方向
     */
    @XmlElement(name = "Entrance", required = true)
    @JsonProperty("Entrance")
    private Direction entrance;

    /**
     * 出口方向
     */
    @XmlElement(name = "Exit", required = true)
    @JsonProperty("Exit")
    private Direction exit;

    // 构造函数
    public UnlockFlowDirection() {
        super();
    }

    public UnlockFlowDirection(String crossId, FlowType type, Direction entrance, Direction exit) {
        super();
        this.crossId = crossId;
        this.type = type;
        this.entrance = entrance;
        this.exit = exit;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public FlowType getType() {
        return type;
    }

    public void setType(FlowType type) {
        this.type = type;
    }

    public Direction getEntrance() {
        return entrance;
    }

    public void setEntrance(Direction entrance) {
        this.entrance = entrance;
    }

    public Direction getExit() {
        return exit;
    }

    public void setExit(Direction exit) {
        this.exit = exit;
    }

    @Override
    public String toString() {
        return "UnlockFlowDirection{" +
                "crossId='" + crossId + '\'' +
                ", type=" + type +
                ", entrance=" + entrance +
                ", exit=" + exit +
                "} " + super.toString();
    }
}
