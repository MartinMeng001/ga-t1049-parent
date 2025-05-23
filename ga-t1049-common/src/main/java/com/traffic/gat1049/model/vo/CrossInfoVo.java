package com.traffic.gat1049.model.vo;

/**
 * 路口信息视图对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrossInfoVo implements Serializable {

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
     * 路口形状
     */
    @JsonProperty("feature")
    private CrossFeature feature;

    /**
     * 路口等级
     */
    @JsonProperty("grade")
    private CrossGrade grade;

    /**
     * 路口状态
     */
    @JsonProperty("state")
    private SystemState state;

    /**
     * 经度
     */
    @JsonProperty("longitude")
    private Double longitude;

    /**
     * 纬度
     */
    @JsonProperty("latitude")
    private Double latitude;

    /**
     * 信号机编号
     */
    @JsonProperty("signalControllerId")
    private String signalControllerId;

    /**
     * 车道数量
     */
    @JsonProperty("laneCount")
    private Integer laneCount;

    /**
     * 信号组数量
     */
    @JsonProperty("signalGroupCount")
    private Integer signalGroupCount;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("lastUpdateTime")
    private LocalDateTime lastUpdateTime;

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

    public SystemState getState() {
        return state;
    }

    public void setState(SystemState state) {
        this.state = state;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getSignalControllerId() {
        return signalControllerId;
    }

    public void setSignalControllerId(String signalControllerId) {
        this.signalControllerId = signalControllerId;
    }

    public Integer getLaneCount() {
        return laneCount;
    }

    public void setLaneCount(Integer laneCount) {
        this.laneCount = laneCount;
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

    @Override
    public String toString() {
        return "CrossInfoVo{" +
                "crossId='" + crossId + '\'' +
                ", crossName='" + crossName + '\'' +
                ", feature=" + feature +
                ", grade=" + grade +
                ", state=" + state +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", signalControllerId='" + signalControllerId + '\'' +
                ", laneCount=" + laneCount +
                ", signalGroupCount=" + signalGroupCount +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
