package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 路口周期
 * 对应文档中的 CrossCycle
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossCycle")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossCycle {//extends BaseState

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 周期开始时间（字符串格式：yyyy-MM-dd HH:mm:ss）
     */
    @NotBlank(message = "开始时间不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$",
            message = "开始时间格式必须为yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "StartTime", required = true)
    @JsonProperty("StartTime")
    private String startTime;

    /**
     * 上周期长度（秒）
     */
    @Min(value = 0, message = "周期长度不能为负数")
    @XmlElement(name = "LastCycleLen", required = true)
    @JsonProperty("LastCycleLen")
    private Integer lastCycleLen;

    // 时间格式化工具
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 构造函数
    public CrossCycle() {
        super();
        this.startTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    public CrossCycle(String crossId, Integer lastCycleLen) {
        super();
        this.crossId = crossId;
        this.startTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        this.lastCycleLen = lastCycleLen;
    }

    public CrossCycle(String crossId, String startTime, Integer lastCycleLen) {
        super();
        this.crossId = crossId;
        this.startTime = startTime;
        this.lastCycleLen = lastCycleLen;
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

    /**
     * 设置开始时间（从LocalDateTime转换）
     * @param startTime LocalDateTime对象
     */
    public void setStartTime(LocalDateTime startTime) {
        if (startTime != null) {
            this.startTime = startTime.format(DATE_TIME_FORMATTER);
        }
    }

    /**
     * 获取开始时间的LocalDateTime对象
     * @return LocalDateTime对象，如果字符串格式错误则返回null
     */
    public LocalDateTime getStartTimeAsLocalDateTime() {
        try {
            return startTime != null ? LocalDateTime.parse(startTime, DATE_TIME_FORMATTER) : null;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getLastCycleLen() {
        return lastCycleLen;
    }

    public void setLastCycleLen(Integer lastCycleLen) {
        this.lastCycleLen = lastCycleLen;
    }

    /**
     * 验证时间格式是否正确
     * @param timeStr 时间字符串
     * @return 是否为有效格式
     */
    public static boolean isValidTimeFormat(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDateTime.parse(timeStr, DATE_TIME_FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将LocalDateTime转换为字符串格式
     * @param dateTime LocalDateTime对象
     * @return 格式化后的时间字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }

    /**
     * 将字符串转换为LocalDateTime
     * @param timeStr 时间字符串
     * @return LocalDateTime对象
     */
    public static LocalDateTime parseDateTime(String timeStr) {
        return timeStr != null ? LocalDateTime.parse(timeStr, DATE_TIME_FORMATTER) : null;
    }

    @Override
    public String toString() {
        return "CrossCycle{" +
                "crossId='" + crossId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", lastCycleLen=" + lastCycleLen +
                "} " + super.toString();
    }
}