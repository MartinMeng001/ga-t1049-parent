package com.traffic.gat1049.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交通流数据查询DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrafficDataQueryDto extends PageRequestDto {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @JsonProperty("crossId")
    private String crossId;

    /**
     * 开始时间
     */
    @JsonProperty("startTime")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonProperty("endTime")
    private LocalDateTime endTime;

    /**
     * 车道序号列表
     */
    @JsonProperty("laneNos")
    private List<Integer> laneNos;

    /**
     * 数据类型
     */
    @JsonProperty("dataType")
    private String dataType;

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getLaneNos() {
        return laneNos;
    }

    public void setLaneNos(List<Integer> laneNos) {
        this.laneNos = laneNos;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "TrafficDataQueryDto{" +
                "crossId='" + crossId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", laneNos=" + laneNos +
                ", dataType='" + dataType + '\'' +
                "} " + super.toString();
    }
}
