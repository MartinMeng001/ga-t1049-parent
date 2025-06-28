package com.traffic.gat1049.protocol.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.DirectionAdapter;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.FlowTypeAdapter;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.LockTypeAdapter;
import com.traffic.gat1049.protocol.model.base.BaseCommand;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.FlowType;
import com.traffic.gat1049.model.enums.LockType;

import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 锁定交通流向命令
 * 对应文档中的 LockFlowDirection
 * 更新版本：支持锁定阶段号字段
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
     * 取值：0-行人，1-机动车，2-非机动车
     */
    @NotNull(message = "交通流类型不能为空")
    @XmlElement(name = "Type", required = true)
    @XmlJavaTypeAdapter(FlowTypeAdapter.class)
    @JsonProperty("Type")
    private FlowType type;

    /**
     * 进口方向
     * 交通流类型为行人时表示人行横道所在方向，取值应符合 GB/T 39900-2021 的 A.18.3
     */
    @NotNull(message = "进口方向不能为空")
    @XmlElement(name = "Entrance", required = true)
    @XmlJavaTypeAdapter(DirectionAdapter.class)
    @JsonProperty("Entrance")
    private Direction entrance;

    /**
     * 出口方向
     * 交通流类型为行人时表示人行横道所在方向，取值应符合 GB/T 39900-2021 的 A.18.3
     */
    @NotNull(message = "出口方向不能为空")
    @XmlElement(name = "Exit", required = true)
    @XmlJavaTypeAdapter(DirectionAdapter.class)
    @JsonProperty("Exit")
    private Direction exit;

    /**
     * 锁定类型
     * 取值：
     * 1-匹配当前方案中放行此流向的阶段（推荐）
     * 2-单个进口方向放行
     * 3-只放行此流向信号组
     * 4-锁定指定阶段
     */
    @NotNull(message = "锁定类型不能为空")
    @XmlElement(name = "LockType", required = true)
    @XmlJavaTypeAdapter(LockTypeAdapter.class)
    @JsonProperty("LockType")
    private LockType lockType;

    /**
     * 锁定阶段号
     * 锁定类型为1-3时，取值0
     * 锁定类型为4时，取值为锁定阶段号
     */
    @NotNull(message = "锁定阶段号不能为空")
    @Min(value = 0, message = "锁定阶段号不能为负数")
    @XmlElement(name = "LockStageNo", required = true)
    @JsonProperty("LockStageNo")
    private Integer lockStageNo;

    /**
     * 锁定持续时长（秒）
     * 取值：0-表示持续锁定，1-3600-锁定持续时长
     */
    @NotNull(message = "锁定持续时长不能为空")
    @Min(value = 0, message = "锁定时间不能为负数")
    @Max(value = 3600, message = "锁定时间不能超过3600秒")
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
        // 设置默认值
        this.lockStageNo = 0;
    }

    public LockFlowDirection(String crossId, FlowType type, Direction entrance, Direction exit,
                             LockType lockType, Integer lockStageNo, Integer duration) {
        super();
        this.crossId = crossId;
        this.type = type;
        this.entrance = entrance;
        this.exit = exit;
        this.lockType = lockType;
        this.lockStageNo = lockStageNo;
        this.duration = duration;
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

    public Integer getLockStageNo() {
        return lockStageNo;
    }

    public void setLockStageNo(Integer lockStageNo) {
        this.lockStageNo = lockStageNo;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * 验证锁定阶段号与锁定类型的一致性
     * @return true 如果验证通过
     */
    public boolean validateLockStageNo() {
        if (lockType == null || lockStageNo == null) {
            return false;
        }

        // 锁定类型为1-3时，lockStageNo应该为0
        if (lockType == LockType.CURRENT_PLAN ||
                lockType == LockType.SINGLE_ENTRANCE ||
                lockType == LockType.SIGNAL_GROUP_ONLY) {
            return lockStageNo == 0;
        }

        // 锁定类型为4时，lockStageNo应该大于0
        if (lockType == LockType.LOCK_STAGE) {
            return lockStageNo > 0;
        }

        return true;
    }

    @Override
    public String toString() {
        return "LockFlowDirection{" +
                "crossId='" + crossId + '\'' +
                ", type=" + type +
                ", entrance=" + entrance +
                ", exit=" + exit +
                ", lockType=" + lockType +
                ", lockStageNo=" + lockStageNo +
                ", duration=" + duration +
                "} " + super.toString();
    }
}