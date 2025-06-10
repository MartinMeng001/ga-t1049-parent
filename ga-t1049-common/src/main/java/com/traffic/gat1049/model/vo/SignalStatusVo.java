package com.traffic.gat1049.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 信号状态视图对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignalStatusVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 路口编号
     */
    @JsonProperty("crossId")
    private String crossId;

    /**
     * 路口名称
     */
    @JsonProperty("crossName")
    private String crossName;

    /**
     * 当前阶段号
     */
    @JsonProperty("currentStageNo")
    private Integer currentStageNo;

    /**
     * 当前阶段名称
     */
    @JsonProperty("currentStageName")
    private String currentStageName;

    /**
     * 当前阶段剩余时间（秒）
     */
    @JsonProperty("remainingTime")
    private Integer remainingTime;

    /**
     * 当前配时方案号
     */
    @JsonProperty("currentPlanNo")
    private Integer currentPlanNo;

    /**
     * 当前配时方案名称
     */
    @JsonProperty("currentPlanName")
    private String currentPlanName;

    /**
     * 周期长度（秒）
     */
    @JsonProperty("cycleLength")
    private Integer cycleLength;

    /**
     * 周期已执行时间（秒）
     */
    @JsonProperty("cycleElapsed")
    private Integer cycleElapsed;

    /**
     * 信号组状态列表
     */
    @JsonProperty("signalGroupStatuses")
    private List<SignalGroupStatusVo> signalGroupStatuses;

    /**
     * 状态更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updateTime")
    private LocalDateTime updateTime;

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

    public Integer getCurrentStageNo() {
        return currentStageNo;
    }

    public void setCurrentStageNo(Integer currentStageNo) {
        this.currentStageNo = currentStageNo;
    }

    public String getCurrentStageName() {
        return currentStageName;
    }

    public void setCurrentStageName(String currentStageName) {
        this.currentStageName = currentStageName;
    }

    public Integer getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Integer remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Integer getCurrentPlanNo() {
        return currentPlanNo;
    }

    public void setCurrentPlanNo(Integer currentPlanNo) {
        this.currentPlanNo = currentPlanNo;
    }

    public String getCurrentPlanName() {
        return currentPlanName;
    }

    public void setCurrentPlanName(String currentPlanName) {
        this.currentPlanName = currentPlanName;
    }

    public Integer getCycleLength() {
        return cycleLength;
    }

    public void setCycleLength(Integer cycleLength) {
        this.cycleLength = cycleLength;
    }

    public Integer getCycleElapsed() {
        return cycleElapsed;
    }

    public void setCycleElapsed(Integer cycleElapsed) {
        this.cycleElapsed = cycleElapsed;
    }

    public List<SignalGroupStatusVo> getSignalGroupStatuses() {
        return signalGroupStatuses;
    }

    public void setSignalGroupStatuses(List<SignalGroupStatusVo> signalGroupStatuses) {
        this.signalGroupStatuses = signalGroupStatuses;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "SignalStatusVo{" +
                "crossId='" + crossId + '\'' +
                ", crossName='" + crossName + '\'' +
                ", currentStageNo=" + currentStageNo +
                ", currentStageName='" + currentStageName + '\'' +
                ", remainingTime=" + remainingTime +
                ", currentPlanNo=" + currentPlanNo +
                ", currentPlanName='" + currentPlanName + '\'' +
                ", cycleLength=" + cycleLength +
                ", cycleElapsed=" + cycleElapsed +
                ", signalGroupStatuses=" + signalGroupStatuses +
                ", updateTime=" + updateTime +
                '}';
    }
}
