package com.traffic.gat1049.protocol.model.traffic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;
import com.traffic.gat1049.protocol.model.base.LocalDateTimeAdapter;

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
public class StageTrafficData {//extends BaseState

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 阶段开始时间
     * 格式: yyyy-MM-dd HH:mm:ss
     */
    @XmlElement(name = "StartTime", required = true)
    @JsonProperty("StartTime")
    private String startTime;

    /**
     * 阶段结束时间
     * 格式: yyyy-MM-dd HH:mm:ss
     */
    @XmlElement(name = "EndTime", required = true)
    @JsonProperty("EndTime")
    private String endTime;

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
        //super();
    }

    public StageTrafficData(String crossId, Integer stageNo) {
        //super();
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    /**
     * 获取开始时间的 LocalDateTime 对象
     * @return LocalDateTime 对象，如果 startTime 为空或格式错误则返回 null
     */
    public LocalDateTime getStartTimeAsLocalDateTime() {
        return parseDateTime(this.startTime);
    }

    /**
     * 设置开始时间，接受 LocalDateTime 参数
     * @param startTime LocalDateTime 对象
     */
    public void setStartTimeAsLocalDateTime(LocalDateTime startTime) {
        this.startTime = formatDateTime(startTime);
    }

    /**
     * 获取结束时间的 LocalDateTime 对象
     * @return LocalDateTime 对象，如果 endTime 为空或格式错误则返回 null
     */
    public LocalDateTime getEndTimeAsLocalDateTime() {
        return parseDateTime(this.endTime);
    }

    /**
     * 设置结束时间，接受 LocalDateTime 参数
     * @param endTime LocalDateTime 对象
     */
    public void setEndTimeAsLocalDateTime(LocalDateTime endTime) {
        this.endTime = formatDateTime(endTime);
    }

    public void setEndTime(String endTime) {
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

    /**
     * 将 LocalDateTime 转换为字符串格式
     * @param dateTime LocalDateTime 对象
     * @return 格式化的时间字符串 (yyyy-MM-dd HH:mm:ss)，如果输入为 null 则返回 null
     */
    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 将字符串格式转换为 LocalDateTime
     * @param dateTimeStr 时间字符串 (yyyy-MM-dd HH:mm:ss)
     * @return LocalDateTime 对象，如果输入为空或格式错误则返回 null
     */
    private static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (java.time.format.DateTimeParseException e) {
            // 如果解析失败，返回 null 而不是抛出异常
            return null;
        }
    }
}
