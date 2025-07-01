package com.traffic.gat1049.protocol.model.traffic;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 路口交通流数据 - 使用 String 时间
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossTrafficData")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossTrafficData  {//extends BaseState
    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 统计截止时间 - 使用字符串格式：yyyy-MM-dd HH:mm:ss
     */
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
            message = "时间格式必须为 yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "EndTime", required = true)
    @JsonProperty("EndTime")
    private String endTime;

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
        this.endTime = getCurrentTimeString();
    }

    public CrossTrafficData(String crossId, Integer interval) {
        super();
        this.crossId = crossId;
        this.interval = interval;
        this.endTime = getCurrentTimeString();
    }

    // 辅助方法：获取当前时间字符串
    private String getCurrentTimeString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // 辅助方法：将字符串转换为 LocalDateTime（需要时使用）
    public LocalDateTime getEndTimeAsLocalDateTime() {
        if (endTime == null || endTime.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // 辅助方法：从 LocalDateTime 设置时间
    public void setEndTimeFromLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            this.endTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
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
                ", endTime='" + endTime + '\'' +
                ", interval=" + interval +
                ", dataList=" + dataList +
                "} ";// + super.toString()
    }
}
