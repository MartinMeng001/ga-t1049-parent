package com.traffic.gat1049.model.vo;

/**
 * 车道统计视图对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LaneStatisticsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车道序号
     */
    @JsonProperty("laneNo")
    private Integer laneNo;

    /**
     * 车道名称
     */
    @JsonProperty("laneName")
    private String laneName;

    /**
     * 交通流量
     */
    @JsonProperty("volume")
    private Integer volume;

    /**
     * 平均速度
     */
    @JsonProperty("speed")
    private Double speed;

    /**
     * 占有率
     */
    @JsonProperty("occupancy")
    private Double occupancy;

    /**
     * 饱和度
     */
    @JsonProperty("saturation")
    private Double saturation;

    /**
     * 排队长度
     */
    @JsonProperty("queueLength")
    private Double queueLength;

    // Getters and Setters
    public Integer getLaneNo() {
        return laneNo;
    }

    public void setLaneNo(Integer laneNo) {
        this.laneNo = laneNo;
    }

    public String getLaneName() {
        return laneName;
    }

    public void setLaneName(String laneName) {
        this.laneName = laneName;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Double occupancy) {
        this.occupancy = occupancy;
    }

    public Double getSaturation() {
        return saturation;
    }

    public void setSaturation(Double saturation) {
        this.saturation = saturation;
    }

    public Double getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(Double queueLength) {
        this.queueLength = queueLength;
    }

    @Override
    public String toString() {
        return "LaneStatisticsVo{" +
                "laneNo=" + laneNo +
                ", laneName='" + laneName + '\'' +
                ", volume=" + volume +
                ", speed=" + speed +
                ", occupancy=" + occupancy +
                ", saturation=" + saturation +
                ", queueLength=" + queueLength +
                '}';
    }
}
