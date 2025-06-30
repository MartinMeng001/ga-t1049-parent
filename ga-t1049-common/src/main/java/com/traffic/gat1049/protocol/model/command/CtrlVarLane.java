package com.traffic.gat1049.protocol.model.command;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.LaneMovementAdapter;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.VarLaneModeAdapter;
import com.traffic.gat1049.model.enums.LaneMovement;
import com.traffic.gat1049.model.enums.VarLaneMode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 设置可变导向车道功能命令
 * 对应文档中的 CtrlVarLane
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CtrlVarLane")
@XmlAccessorType(XmlAccessType.FIELD)
public class CtrlVarLane {

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
    @Min(value = 1, message = "车道序号最小值为1")
    @Max(value = 99, message = "车道序号最大值为99")
    @XmlElement(name = "LaneNo", required = true)
    @JsonProperty("LaneNo")
    private Integer laneNo;

    /**
     * 设置的功能（转向）
     */
    @NotNull(message = "车道转向不能为空")
    @XmlElement(name = "Movement", required = true)
    @XmlJavaTypeAdapter(LaneMovementAdapter.class)
    @JsonProperty("Movement")
    private LaneMovement movement;

    /**
     * 可变导向车道控制方式
     */
    @NotNull(message = "控制方式不能为空")
    @XmlElement(name = "CtrlMode", required = true)
    @XmlJavaTypeAdapter(VarLaneModeAdapter.class)
    @JsonProperty("CtrlMode")
    private VarLaneMode ctrlMode;

    /**
     * 开始时间
     * 符合 GA/T 543.6 的 DE005540554
     * CtrlMode为00时该值无意义
     */
    @XmlElement(name = "StartTime")
    @JsonProperty("StartTime")
    private String startTime;

    /**
     * 结束时间
     * 符合 GA/T 543.6 的 DE005540554
     * CtrlMode为00时该值无意义
     */
    @XmlElement(name = "EndTime")
    @JsonProperty("EndTime")
    private String endTime;

    // 构造函数
    public CtrlVarLane() {}

    public CtrlVarLane(String crossId, Integer laneNo, LaneMovement movement, VarLaneMode ctrlMode) {
        this.crossId = crossId;
        this.laneNo = laneNo;
        this.movement = movement;
        this.ctrlMode = ctrlMode;
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

    public LaneMovement getMovement() {
        return movement;
    }

    public void setMovement(LaneMovement movement) {
        this.movement = movement;
    }

    public VarLaneMode getCtrlMode() {
        return ctrlMode;
    }

    public void setCtrlMode(VarLaneMode ctrlMode) {
        this.ctrlMode = ctrlMode;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "CtrlVarLane{" +
                "crossId='" + crossId + '\'' +
                ", laneNo=" + laneNo +
                ", movement=" + movement +
                ", ctrlMode=" + ctrlMode +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
