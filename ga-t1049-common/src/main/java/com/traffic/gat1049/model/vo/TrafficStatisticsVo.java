package com.traffic.gat1049.model.vo;

/**
 * 交通流统计视图对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrafficStatisticsVo implements Serializable {

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
     * 统计时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("statisticsTime")
    private LocalDateTime statisticsTime;

    /**
     * 总流量
     */
    @JsonProperty("totalVolume")
    private Integer totalVolume;

    /**
     * 平均速度
     */
    @JsonProperty("avgSpeed")
    private Double avgSpeed;

    /**
     * 平均占有率
     */
    @JsonProperty("avgOccupancy")
    private Double avgOccupancy;

    /**
     * 平均饱和度
     */
    @JsonProperty("avgSaturation")
    private Double avgSaturation;

    /**
     * 车道统计详情
     */
    @JsonProperty("laneStatistics")
    private List<LaneStatisticsVo> laneStatistics;

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

    public LocalDateTime getStatisticsTime() {
        return statisticsTime;
    }

    public void setStatisticsTime(LocalDateTime statisticsTime) {
        this.statisticsTime = statisticsTime;
    }

    public Integer getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Integer totalVolume) {
        this.totalVolume = totalVolume;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Double getAvgOccupancy() {
        return avgOccupancy;
    }

    public void setAvgOccupancy(Double avgOccupancy) {
        this.avgOccupancy = avgOccupancy;
    }

    public Double getAvgSaturation() {
        return avgSaturation;
    }

    public void setAvgSaturation(Double avgSaturation) {
        this.avgSaturation = avgSaturation;
    }

    public List<LaneStatisticsVo> getLaneStatistics() {
        return laneStatistics;
    }

    public void setLaneStatistics(List<LaneStatisticsVo> laneStatistics) {
        this.laneStatistics = laneStatistics;
    }

    @Override
    public String toString() {
        return "TrafficStatisticsVo{" +
                "crossId='" + crossId + '\'' +
                ", crossName='" + crossName + '\'' +
                ", statisticsTime=" + statisticsTime +
                ", totalVolume=" + totalVolume +
                ", avgSpeed=" + avgSpeed +
                ", avgOccupancy=" + avgOccupancy +
                ", avgSaturation=" + avgSaturation +
                ", laneStatistics=" + laneStatistics +
                '}';
    }
}
