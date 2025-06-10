package com.traffic.gat1049.model.entity.traffic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.entity.base.BaseState;
import com.traffic.gat1049.model.entity.base.LocalDateTimeAdapter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 阶段交通流数据
 * 对应文档中的 StageTrafficData
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "StageTrafficData")
@XmlAccessorType(XmlAccessType.FIELD)
public class StageTrafficData extends BaseState {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 阶段开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "StartTime", required = true)
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    @JsonProperty("StartTime")
    private LocalDateTime startTime;

    /**
     * 阶段结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "EndTime", required = true)
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    @JsonProperty("EndTime")
    private LocalDateTime endTime;

    /**
     * 阶段号
     */
    @NotNull(message = "阶段号不能为空")
    @XmlElement(name = "StageNo", required = true)
    @JsonProperty("StageNo")
    private Integer stageNo;

    /**
     * 车道交通流量数据列表
     */
    @NotEmpty(message = "车道交通流量数据列表不能为空")
    @XmlElementWrapper(name = "DataList")
    @XmlElement(name = "Data")
    @JsonProperty("DataList")
    private List<StageTrafficFlowData> dataList = new ArrayList<>();

    // 构造函数
    public StageTrafficData() {
        super();
    }

    public StageTrafficData(String crossId, Integer stageNo) {
        super();
        this.crossId = crossId;
        this.stageNo = stageNo;
        this.startTime = LocalDateTime.now();
    }

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

    public Integer getStageNo() {
        return stageNo;
    }

    public void setStageNo(Integer stageNo) {
        this.stageNo = stageNo;
    }

    public List<StageTrafficFlowData> getDataList() {
        return dataList;
    }

    public void setDataList(List<StageTrafficFlowData> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "StageTrafficData{" +
                "crossId='" + crossId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", stageNo=" + stageNo +
                ", dataList=" + dataList +
                "} " + super.toString();
    }
}
