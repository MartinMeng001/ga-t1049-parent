package com.traffic.gat1049.model.entity.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.entity.base.BaseCommand;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.FlowType;
import com.traffic.gat1049.model.enums.LockType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 锁定交通流向命令
 * 对应文档中的 LockFlowDirection
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "LockFlowDirection")
@XmlAccessorType(XmlAccessType.FIELD)
public class LockFlowDirection extends BaseCommand {

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

    /**
     * 锁定阶段类型
     */
    @XmlElement(name = "LockType", required = true)
    @JsonProperty("LockType")
    private LockType lockType;

    /**
     * 锁定持续时间（秒），取值0表示持续锁定
     */
    @Min(value = 0, message = "锁定时间不能为负数")
    @XmlElement(name = "Duration", required = true)
    @JsonProperty("Duration")
    private Integer duration;

    // 构造函数
    public LockFlowDirection() {
        super();
    }

    public LockFlowDirection(String crossId, FlowType type, Direction entrance, Direction exit) {
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

    public LockType getLockType() {
        return lockType;
    }

    public void setLockType(LockType lockType) {
        this.lockType = lockType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "LockFlowDirection{" +
                "crossId='" + crossId + '\'' +
                ", type=" + type +
                ", entrance=" + entrance +
                ", exit=" + exit +
                ", lockType=" + lockType +
                ", duration=" + duration +
                "} " + super.toString();
    }
}
