package com.traffic.gat1049.protocol.model.intersection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseParam;
import com.traffic.gat1049.model.enums.DetectorPosition;
import com.traffic.gat1049.model.enums.DetectorType;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 检测器参数
 * 对应文档中的 DetectorParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "DetectorParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class DetectorParam  {//extends BaseParam

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 检测器序号 - 取值从1开始，3位数字
     */
    @NotNull(message = "检测器序号不能为空")
    @Min(value = 1, message = "检测器序号最小值为1")
    @Max(value = 999, message = "检测器序号最大值为999")
    @XmlElement(name = "DetectorNo", required = true)
    @JsonProperty("DetectorNo")
    private Integer detectorNo;

    /**
     * 检测器类型
     */
    @XmlElement(name = "Type", required = true)
    @JsonProperty("Type")
    private DetectorType type;

    /**
     * 检测位置
     */
    @XmlElement(name = "Position", required = true)
    @JsonProperty("Position")
    private DetectorPosition position;

    /**
     * 检测对象 - 长度三位，由左到右分别标记机动车、非机动车、行人
     * 1：支持该类对象检测，0：不支持该类对象检测
     */
    @Pattern(regexp = "[01]{3}", message = "检测对象格式错误，应为3位0或1的组合")
    @XmlElement(name = "Target", required = true)
    @JsonProperty("Target")
    private String target;

    /**
     * 距停车线距离（厘米）
     */
    @Min(value = 0, message = "距离不能为负数")
    @XmlElement(name = "Distance", required = true)
    @JsonProperty("Distance")
    private Integer distance;

    /**
     * 车道序号列表
     */
    @XmlElementWrapper(name = "LaneNoList")
    @XmlElement(name = "LaneNo")
    @JsonProperty("LaneNoList")
    private List<Integer> laneNoList = new ArrayList<>();

    /**
     * 人行横道序号列表
     */
    @XmlElementWrapper(name = "PedestrianNoList")
    @XmlElement(name = "PedestrianNo")
    @JsonProperty("PedestrianNoList")
    private List<Integer> pedestrianNoList = new ArrayList<>();

    // 构造函数
    public DetectorParam() {
        super();
    }

    public DetectorParam(String crossId, Integer detectorNo, DetectorType type) {
        super();
        this.crossId = crossId;
        this.detectorNo = detectorNo;
        this.type = type;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getDetectorNo() {
        return detectorNo;
    }

    public void setDetectorNo(Integer detectorNo) {
        this.detectorNo = detectorNo;
    }

    public DetectorType getType() {
        return type;
    }

    public void setType(DetectorType type) {
        this.type = type;
    }

    public DetectorPosition getPosition() {
        return position;
    }

    public void setPosition(DetectorPosition position) {
        this.position = position;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public List<Integer> getLaneNoList() {
        return laneNoList;
    }

    public void setLaneNoList(List<Integer> laneNoList) {
        this.laneNoList = laneNoList;
    }

    public List<Integer> getPedestrianNoList() {
        return pedestrianNoList;
    }

    public void setPedestrianNoList(List<Integer> pedestrianNoList) {
        this.pedestrianNoList = pedestrianNoList;
    }

    @Override
    public String toString() {
        return "DetectorParam{" +
                "crossId='" + crossId + '\'' +
                ", detectorNo=" + detectorNo +
                ", type=" + type +
                ", position=" + position +
                ", target='" + target + '\'' +
                ", distance=" + distance +
                ", laneNoList=" + laneNoList +
                ", pedestrianNoList=" + pedestrianNoList +
                "} " + super.toString();
    }
}
