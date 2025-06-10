package com.traffic.gat1049.model.entity.intersection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.entity.base.BaseParam;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LaneAttribute;
import com.traffic.gat1049.model.enums.LaneFeature;
import com.traffic.gat1049.model.enums.LaneMovement;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 车道参数
 * 对应文档中的 LaneParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "LaneParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class LaneParam extends BaseParam {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 车道序号 - 取值从1开始，2位数字
     */
    @NotNull(message = "车道序号不能为空")
    @Min(value = 1, message = "车道序号最小值为1")
    @Max(value = 99, message = "车道序号最大值为99")
    @XmlElement(name = "LaneNo", required = true)
    @JsonProperty("LaneNo")
    private Integer laneNo;

    /**
     * 进口车道所在的方向
     */
    @XmlElement(name = "Direction", required = true)
    @JsonProperty("Direction")
    private Direction direction;

    /**
     * 车道属性
     */
    @XmlElement(name = "Attribute", required = true)
    @JsonProperty("Attribute")
    private LaneAttribute attribute;

    /**
     * 转向
     */
    @XmlElement(name = "Movement", required = true)
    @JsonProperty("Movement")
    private LaneMovement movement;

    /**
     * 车道特性
     */
    @XmlElement(name = "Feature", required = true)
    @JsonProperty("Feature")
    private LaneFeature feature;

    /**
     * 方位角 - 以地理正北方向为起点顺时针旋转到进口行驶方向所旋转的角度
     * 单位为度（°），取值0-359
     */
    @Min(value = 0, message = "方位角最小值为0")
    @Max(value = 359, message = "方位角最大值为359")
    @XmlElement(name = "Azimuth")
    @JsonProperty("Azimuth")
    private Integer azimuth;

    /**
     * 待行区 - 0：无待行区，1：有待行区
     */
    @XmlElement(name = "WaitingArea")
    @JsonProperty("WaitingArea")
    private Integer waitingArea;

    /**
     * 可变转向列表
     */
    @XmlElementWrapper(name = "VarMovementList")
    @XmlElement(name = "Movement")
    @JsonProperty("VarMovementList")
    private List<LaneMovement> varMovementList = new ArrayList<>();

    // 构造函数
    public LaneParam() {
        super();
    }

    public LaneParam(String crossId, Integer laneNo, Direction direction) {
        super();
        this.crossId = crossId;
        this.laneNo = laneNo;
        this.direction = direction;
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

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public LaneAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(LaneAttribute attribute) {
        this.attribute = attribute;
    }

    public LaneMovement getMovement() {
        return movement;
    }

    public void setMovement(LaneMovement movement) {
        this.movement = movement;
    }

    public LaneFeature getFeature() {
        return feature;
    }

    public void setFeature(LaneFeature feature) {
        this.feature = feature;
    }

    public Integer getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(Integer azimuth) {
        this.azimuth = azimuth;
    }

    public Integer getWaitingArea() {
        return waitingArea;
    }

    public void setWaitingArea(Integer waitingArea) {
        this.waitingArea = waitingArea;
    }

    public List<LaneMovement> getVarMovementList() {
        return varMovementList;
    }

    public void setVarMovementList(List<LaneMovement> varMovementList) {
        this.varMovementList = varMovementList;
    }

    @Override
    public String toString() {
        return "LaneParam{" +
                "crossId='" + crossId + '\'' +
                ", laneNo=" + laneNo +
                ", direction=" + direction +
                ", attribute=" + attribute +
                ", movement=" + movement +
                ", feature=" + feature +
                ", azimuth=" + azimuth +
                ", waitingArea=" + waitingArea +
                ", varMovementList=" + varMovementList +
                "} " + super.toString();
    }
}
