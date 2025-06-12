package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;
import com.traffic.gat1049.model.enums.LaneMovement;
import com.traffic.gat1049.model.enums.VarLaneMode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 可变车道运行信息
 * 对应文档中的 VarLaneStatus
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "VarLaneStatus")
@XmlAccessorType(XmlAccessType.FIELD)
public class VarLaneStatus extends BaseState {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 车道序号
     */
    @NotNull(message = "车道序号不能为空")
    @XmlElement(name = "LaneNo", required = true)
    @JsonProperty("LaneNo")
    private Integer laneNo;

    /**
     * 当前转向
     */
    @XmlElement(name = "CurMovement", required = true)
    @JsonProperty("CurMovement")
    private LaneMovement curMovement;

    /**
     * 当前控制方式
     */
    @XmlElement(name = "CurMode", required = true)
    @JsonProperty("CurMode")
    private VarLaneMode curMode;

    // 构造函数
    public VarLaneStatus() {
        super();
    }

    public VarLaneStatus(String crossId, Integer laneNo, LaneMovement curMovement) {
        super();
        this.crossId = crossId;
        this.laneNo = laneNo;
        this.curMovement = curMovement;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getLaneNo() {
        return laneNo;
    }

    public void setLaneNo(Integer laneNo) {
        this.laneNo = laneNo;
    }

    public LaneMovement getCurMovement() {
        return curMovement;
    }

    public void setCurMovement(LaneMovement curMovement) {
        this.curMovement = curMovement;
    }

    public VarLaneMode getCurMode() {
        return curMode;
    }

    public void setCurMode(VarLaneMode curMode) {
        this.curMode = curMode;
    }

    @Override
    public String toString() {
        return "VarLaneStatus{" +
                "crossId='" + crossId + '\'' +
                ", laneNo=" + laneNo +
                ", curMovement=" + curMovement +
                ", curMode=" + curMode +
                "} " + super.toString();
    }
}
