package com.traffic.gat1049.model.entity.intersection;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.entity.base.BaseLocationEntity;
import com.traffic.gat1049.model.entity.base.BaseParam;
import com.traffic.gat1049.model.enums.*;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 路口参数
 * 对应文档中的 CrossParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossParam extends BaseLocationEntity {

    /**
     * 路口编号 - 全局唯一，取值区域编号+5位数字
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 路口名称
     */
    @NotBlank(message = "路口名称不能为空")
    @XmlElement(name = "CrossName", required = true)
    @JsonProperty("CrossName")
    private String crossName;

    /**
     * 路口形状
     */
    @XmlElement(name = "Feature", required = true)
    @JsonProperty("Feature")
    private CrossFeature feature;

    /**
     * 路口等级
     */
    @XmlElement(name = "Grade", required = true)
    @JsonProperty("Grade")
    private CrossGrade grade;

    /**
     * 检测器序号列表
     */
    @XmlElementWrapper(name = "DetNoList")
    @XmlElement(name = "DetNo")
    @JsonProperty("DetNoList")
    private List<Integer> detNoList = new ArrayList<>();

    /**
     * 车道序号列表
     */
    @NotEmpty(message = "车道序号列表不能为空")
    @XmlElementWrapper(name = "LaneNoList")
    @XmlElement(name = "LaneNo")
    @JsonProperty("LaneNoList")
    private List<Integer> laneNoList = new ArrayList<>();

    /**
     * 信号灯组序号列表
     */
    @NotEmpty(message = "信号灯组序号列表不能为空")
    @XmlElementWrapper(name = "LampGroupNoList")
    @XmlElement(name = "LampGroupNo")
    @JsonProperty("LampGroupNoList")
    private List<Integer> lampGroupNoList = new ArrayList<>();

    /**
     * 信号组序号列表
     */
    @NotEmpty(message = "信号组序号列表不能为空")
    @XmlElementWrapper(name = "SignalGroupNoList")
    @XmlElement(name = "SignalGroupNo")
    @JsonProperty("SignalGroupNoList")
    private List<Integer> signalGroupNoList = new ArrayList<>();

    /**
     * 阶段号列表
     */
    @NotEmpty(message = "阶段号列表不能为空")
    @XmlElementWrapper(name = "StageNoList")
    @XmlElement(name = "StageNo")
    @JsonProperty("StageNoList")
    private List<Integer> stageNoList = new ArrayList<>();

    /**
     * 配时方案序号列表
     */
    @NotEmpty(message = "配时方案序号列表不能为空")
    @XmlElementWrapper(name = "PlanNoList")
    @XmlElement(name = "PlanNo")
    @JsonProperty("PlanNoList")
    private List<Integer> planNoList = new ArrayList<>();

    /**
     * 日计划序号列表
     */
    @XmlElementWrapper(name = "DayPlanNoList")
    @XmlElement(name = "DayPlanNo")
    @JsonProperty("DayPlanNoList")
    private List<Integer> dayPlanNoList = new ArrayList<>();

    /**
     * 调度序号列表
     */
    @XmlElementWrapper(name = "ScheduleNoList")
    @XmlElement(name = "ScheduleNo")
    @JsonProperty("ScheduleNoList")
    private List<Integer> scheduleNoList = new ArrayList<>();

    // 构造函数
    public CrossParam() {
        super();
    }

    public CrossParam(String crossId, String crossName) {
        super();
        this.crossId = crossId;
        this.crossName = crossName;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public String getCrossName() {
        return crossName;
    }

    public void setCrossName(String crossName) {
        this.crossName = crossName;
    }

    public CrossFeature getFeature() {
        return feature;
    }

    public void setFeature(CrossFeature feature) {
        this.feature = feature;
    }

    public CrossGrade getGrade() {
        return grade;
    }

    public void setGrade(CrossGrade grade) {
        this.grade = grade;
    }

    public List<Integer> getDetNoList() {
        return detNoList;
    }

    public void setDetNoList(List<Integer> detNoList) {
        this.detNoList = detNoList;
    }

    public List<Integer> getLaneNoList() {
        return laneNoList;
    }

    public void setLaneNoList(List<Integer> laneNoList) {
        this.laneNoList = laneNoList;
    }

    public List<Integer> getLampGroupNoList() {
        return lampGroupNoList;
    }

    public void setLampGroupNoList(List<Integer> lampGroupNoList) {
        this.lampGroupNoList = lampGroupNoList;
    }

    public List<Integer> getSignalGroupNoList() {
        return signalGroupNoList;
    }

    public void setSignalGroupNoList(List<Integer> signalGroupNoList) {
        this.signalGroupNoList = signalGroupNoList;
    }

    public List<Integer> getStageNoList() {
        return stageNoList;
    }

    public void setStageNoList(List<Integer> stageNoList) {
        this.stageNoList = stageNoList;
    }

    public List<Integer> getPlanNoList() {
        return planNoList;
    }

    public void setPlanNoList(List<Integer> planNoList) {
        this.planNoList = planNoList;
    }

    public List<Integer> getDayPlanNoList() {
        return dayPlanNoList;
    }

    public void setDayPlanNoList(List<Integer> dayPlanNoList) {
        this.dayPlanNoList = dayPlanNoList;
    }

    public List<Integer> getScheduleNoList() {
        return scheduleNoList;
    }

    public void setScheduleNoList(List<Integer> scheduleNoList) {
        this.scheduleNoList = scheduleNoList;
    }

    @Override
    public String toString() {
        return "CrossParam{" +
                "crossId='" + crossId + '\'' +
                ", crossName='" + crossName + '\'' +
                ", feature=" + feature +
                ", grade=" + grade +
                ", detNoList=" + detNoList +
                ", laneNoList=" + laneNoList +
                ", lampGroupNoList=" + lampGroupNoList +
                ", signalGroupNoList=" + signalGroupNoList +
                ", stageNoList=" + stageNoList +
                ", planNoList=" + planNoList +
                ", dayPlanNoList=" + dayPlanNoList +
                ", scheduleNoList=" + scheduleNoList +
                "} " + super.toString();
    }
}
