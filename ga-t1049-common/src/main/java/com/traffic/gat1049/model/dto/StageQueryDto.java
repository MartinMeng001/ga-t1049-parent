package com.traffic.gat1049.model.dto;

/**
 * 阶段查询条件DTO
 */
public class StageQueryDto {

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
     * 信号组编号（用于查询包含特定信号组的阶段）
     */
    private Integer signalGroupNo;

    // 构造函数
    public StageQueryDto() {
    }

    public StageQueryDto(String crossId) {
        this.crossId = crossId;
    }

    public StageQueryDto(String crossId, Integer stageNo) {
        this.crossId = crossId;
        this.stageNo = stageNo;
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

    public Integer getSignalGroupNo() {
        return signalGroupNo;
    }

    public void setSignalGroupNo(Integer signalGroupNo) {
        this.signalGroupNo = signalGroupNo;
    }

    @Override
    public String toString() {
        return "StageQueryDto{" +
                "crossId='" + crossId + '\'' +
                ", stageNo=" + stageNo +
                ", stageName='" + stageName + '\'' +
                ", attribute=" + attribute +
                ", signalGroupNo=" + signalGroupNo +
                '}';
    }
}