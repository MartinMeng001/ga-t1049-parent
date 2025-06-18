package com.traffic.gat1049.model.vo;

import java.time.LocalDateTime;

/**
 * 阶段信息VO
 */
public class StageInfoVo {

    /**
     * 路口编号
     */
    private String crossId;

    /**
     * 阶段号
     */
    private Integer stageNo;

    /**
     * 阶段名称
     */
    private String stageName;

    /**
     * 特征 - 0：一般，1：感应
     */
    private Integer attribute;

    /**
     * 信号组数量
     */
    private Integer signalGroupCount;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

    /**
     * 阶段状态描述
     */
    private String statusDescription;

    // 构造函数
    public StageInfoVo() {
    }

    public StageInfoVo(String crossId, Integer stageNo, String stageName) {
        this.crossId = crossId;
        this.stageNo = stageNo;
        this.stageName = stageName;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getStageNo() {
        return stageNo;
    }

    public void setStageNo(Integer stageNo) {
        this.stageNo = stageNo;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public Integer getAttribute() {
        return attribute;
    }

    public void setAttribute(Integer attribute) {
        this.attribute = attribute;
    }

    public Integer getSignalGroupCount() {
        return signalGroupCount;
    }

    public void setSignalGroupCount(Integer signalGroupCount) {
        this.signalGroupCount = signalGroupCount;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    /**
     * 获取特征描述
     */
    public String getAttributeDescription() {
        if (attribute == null) {
            return "未知";
        }
        switch (attribute) {
            case 0:
                return "一般";
            case 1:
                return "感应";
            default:
                return "未知";
        }
    }

    @Override
    public String toString() {
        return "StageInfoVo{" +
                "crossId='" + crossId + '\'' +
                ", stageNo=" + stageNo +
                ", stageName='" + stageName + '\'' +
                ", attribute=" + attribute +
                ", signalGroupCount=" + signalGroupCount +
                ", lastUpdateTime=" + lastUpdateTime +
                ", statusDescription='" + statusDescription + '\'' +
                '}';
    }
}