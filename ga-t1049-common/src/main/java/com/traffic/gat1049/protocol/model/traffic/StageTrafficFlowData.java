package com.traffic.gat1049.protocol.model.traffic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;

/**
 * 阶段交通流量数据
 * 对应文档中的阶段交通流数据的Data部分
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class StageTrafficFlowData {

    /**
     * 车道序号
     */
    @NotNull(message = "车道序号不能为空")
    @XmlElement(name = "LaneNo", required = true)
    @JsonProperty("LaneNo")
    private Integer laneNo;

    /**
     * 过车数量（辆）
     */
    @NotNull(message = "过车数量不能为空")
    @Min(value = 0, message = "过车数量不能为负数")
    @XmlElement(name = "VehicleNum", required = true)
    @JsonProperty("VehicleNum")
    private Integer vehicleNum;

    /**
     * 小客车当量（pcu/小时）
     */
    @Min(value = 0, message = "小客车当量不能为负数")
    @XmlElement(name = "Pcu")
    @JsonProperty("Pcu")
    private Integer pcu;

    /**
     * 平均车头时距（秒/辆）
     */
    @DecimalMin(value = "0.0", message = "平均车头时距不能为负数")
    @XmlElement(name = "HeadTime")
    @JsonProperty("HeadTime")
    private BigDecimal headTime;

    /**
     * 饱和度（百分率）
     */
    @DecimalMin(value = "0.0", message = "饱和度不能为负数")
    @DecimalMax(value = "100.0", message = "饱和度不能超过100")
    @XmlElement(name = "Saturation")
    @JsonProperty("Saturation")
    private BigDecimal saturation;

    /**
     * 阶段结束时排队长度（米）
     */
    @DecimalMin(value = "0.0", message = "排队长度不能为负数")
    @XmlElement(name = "QueueLength")
    @JsonProperty("QueueLength")
    private BigDecimal queueLength;

    /**
     * 占有率（百分率，0~100）
     */
    @NotNull(message = "占有率不能为空")
    @Min(value = 0, message = "占有率不能为负数")
    @Max(value = 100, message = "占有率不能超过100")
    @XmlElement(name = "Occupancy", required = true)
    @JsonProperty("Occupancy")
    private Integer occupancy;

    // 构造函数
    public StageTrafficFlowData() {}

    public StageTrafficFlowData(Integer laneNo, Integer vehicleNum, Integer occupancy) {
        this.laneNo = laneNo;
        this.vehicleNum = vehicleNum;
        this.occupancy = occupancy;
    }

    // Getters and Setters
    public Integer getLaneNo() {
        return laneNo;
    }

    public void setLaneNo(Integer laneNo) {
        this.laneNo = laneNo;
    }

    public Integer getVehicleNum() {
        return vehicleNum;
    }

    public void setVehicleNum(Integer vehicleNum) {
        this.vehicleNum = vehicleNum;
    }

    public Integer getPcu() {
        return pcu;
    }

    public void setPcu(Integer pcu) {
        this.pcu = pcu;
    }

    public BigDecimal getHeadTime() {
        return headTime;
    }

    public void setHeadTime(BigDecimal headTime) {
        this.headTime = headTime;
    }

    public BigDecimal getSaturation() {
        return saturation;
    }

    public void setSaturation(BigDecimal saturation) {
        this.saturation = saturation;
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
        return "StageTrafficFlowData{" +
                "laneNo=" + laneNo +
                ", vehicleNum=" + vehicleNum +
                ", pcu=" + pcu +
                ", headTime=" + headTime +
                ", saturation=" + saturation +
                ", queueLength=" + queueLength +
                ", occupancy=" + occupancy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StageTrafficFlowData that = (StageTrafficFlowData) o;

        return laneNo != null ? laneNo.equals(that.laneNo) : that.laneNo == null;
    }

    @Override
    public int hashCode() {
        return laneNo != null ? laneNo.hashCode() : 0;
    }
}