package com.traffic.gat1049.protocol.model.traffic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;
import com.traffic.gat1049.protocol.model.base.LocalDateTimeAdapter;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 路口交通流数据
 * 对应文档中的 CrossTrafficData
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossTrafficData")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossTrafficData extends BaseState {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 统计截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "EndTime", required = true)
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    @JsonProperty("EndTime")
    private LocalDateTime endTime;

    /**
     * 时间间隔（秒）
     */
    @NotNull(message = "时间间隔不能为空")
    @Min(value = 1, message = "时间间隔最小值为1秒")
    @XmlElement(name = "Interval", required = true)
    @JsonProperty("Interval")
    private Integer interval;

    /**
     * 车道交通流数据列表
     */
    @NotEmpty(message = "车道交通流数据列表不能为空")
    @XmlElementWrapper(name = "DataList")
    @XmlElement(name = "Data")
    @JsonProperty("DataList")
    private List<LaneTrafficData> dataList = new ArrayList<>();

    // 构造函数
    public CrossTrafficData() {
        super();
        this.endTime = LocalDateTime.now();
    }

    public CrossTrafficData(String crossId, Integer interval) {
        super();
        this.crossId = crossId;
        this.interval = interval;
        this.endTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public List<LaneTrafficData> getDataList() {
        return dataList;
    }

    public void setDataList(List<LaneTrafficData> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "CrossTrafficData{" +
                "crossId='" + crossId + '\'' +
                ", endTime=" + endTime +
                ", interval=" + interval +
                ", dataList=" + dataList +
                "} " + super.toString();
    }
}
