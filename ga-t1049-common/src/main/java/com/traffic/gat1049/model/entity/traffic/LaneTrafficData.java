package com.traffic.gat1049.model.entity.traffic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

/**
 * 车道交通流数据
 * 包含单个车道的交通流统计信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class LaneTrafficData {

    /**
     * 车道序号
     */
    @NotNull(message = "车道序号不能为空")
    @XmlElement(name = "LaneNo", required = true)
    @JsonProperty("LaneNo")
    private Integer laneNo;

    /**
     * 交通流量（辆/小时）
     */
    @Min(value = 0, message = "交通流量不能为负数")
    @XmlElement(name = "Volume", required = true)
    @JsonProperty("Volume")
    private Integer volume;

    /**
     * 平均车长（米）
     */
    @DecimalMin(value = "0.0", message = "平均车长不能为负数")
    @XmlElement(name = "AvgVehLen")
    @JsonProperty("AvgVehLen")
    private BigDecimal avgVehLen;

    /**
     * 小客车当量（pcu/小时）
     */
    @Min(value = 0, message = "小客车当量不能为负数")
    @XmlElement(name = "Pcu")
    @JsonProperty("Pcu")
    private Integer pcu;

    /**
     * 平均车头间距（米/辆）
     */
    @DecimalMin(value = "0.0", message = "平均车头间距不能为负数")
    @XmlElement(name = "HeadDistance")
    @JsonProperty("HeadDistance")
    private BigDecimal headDistance;

    /**
     * 平均车头时距（秒/辆）
     */
    @DecimalMin(value = "0.0", message = "平均车头时距不能为负数")
    @XmlElement(name = "HeadTime")
    @JsonProperty("HeadTime")
    private BigDecimal headTime;

    /**
     * 平均速度（公里/小时）
     */
    @DecimalMin(value = "0.0", message = "平均速度不能为负数")
    @XmlElement(name = "Speed")
    @JsonProperty("Speed")
    private BigDecimal speed;

    /**
     * 饱和度（百分率）
     */
    @DecimalMin(value = "0.0", message = "饱和度不能为负数")
    @DecimalMax(value = "100.0", message = "饱和度不能超过100")
    @XmlElement(name = "Saturation")
    @JsonProperty("Saturation")
    private BigDecimal saturation;

    /**
     * 密度（辆/公里）
     */
    @Min(value = 0, message = "密度不能为负数")
    @XmlElement(name = "Density")
    @JsonProperty("Density")
    private Integer density;

    /**
     * 平均排队长度（米）
     */
    @DecimalMin(value = "0.0", message = "平均排队长度不能为负数")
    @XmlElement(name = "QueueLength")
    @JsonProperty("QueueLength")
    private BigDecimal queueLength;

    /**
     * 占有率（百分率，0~100）
     */
    @Min(value = 0, message = "占有率不能为负数")
    @Max(value = 100, message = "占有率不能超过100")
    @XmlElement(name = "Occupancy", required = true)
    @JsonProperty("Occupancy")
    private Integer occupancy;

    // 构造函数
    public LaneTrafficData() {}

    public LaneTrafficData(Integer laneNo, Integer volume, Integer occupancy) {
        this.laneNo = laneNo;
        this.volume = volume;
        this.occupancy = occupancy;
    }

    // Getters and Setters
    public Integer getLaneNo() {
        return laneNo;
    }

    public void setLaneNo(Integer laneNo) {
        this.laneNo = laneNo;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public BigDecimal getAvgVehLen() {
        return avgVehLen;
    }

    public void setAvgVehLen(BigDecimal avgVehLen) {
        this.avgVehLen = avgVehLen;
    }

    public Integer getPcu() {
        return pcu;
    }

    public void setPcu(Integer pcu) {
        this.pcu = pcu;
    }

    public BigDecimal getHeadDistance() {
        return headDistance;
    }

    public void setHeadDistance(BigDecimal headDistance) {
        this.headDistance = headDistance;
    }

    public BigDecimal getHeadTime() {
        return headTime;
    }

    public void setHeadTime(BigDecimal headTime) {
        this.headTime = headTime;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal speed) {
        this.speed = speed;
    }

    public BigDecimal getSaturation() {
        return saturation;
    }

    public void setSaturation(BigDecimal saturation) {
        this.saturation = saturation;
    }

    public Integer getDensity() {
        return density;
    }

    public void setDensity(Integer density) {
        this.density = density;
    }

    public BigDecimal getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(BigDecimal queueLength) {
        this.queueLength = queueLength;
    }

    public Integer getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Integer occupancy) {
        this.occupancy = occupancy;
    }

    @Override
    public String toString() {
        return "LaneTrafficData{" +
                "laneNo=" + laneNo +
                ", volume=" + volume +
                ", avgVehLen=" + avgVehLen +
                ", pcu=" + pcu +
                ", headDistance=" + headDistance +
                ", headTime=" + headTime +
                ", speed=" + speed +
                ", saturation=" + saturation +
                ", density=" + density +
                ", queueLength=" + queueLength +
                ", occupancy=" + occupancy +
                '}';
    }
}
