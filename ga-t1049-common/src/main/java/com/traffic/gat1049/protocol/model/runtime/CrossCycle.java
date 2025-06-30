package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 路口周期
 * 对应文档中的 CrossCycle (5.2.5路口周期)
 * 符合 GA/T 1049.2 标准 B.2.5 路口周期定义
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossCycle")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossCycle {//extends BaseState

    /**
     * 路口编号
     * 取值同表B.6中路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 周期开始时间
     * 符合GA/T 543.6的DE0 0554格式要求
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @NotBlank(message = "开始时间不能为空")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$",
            message = "开始时间格式必须为yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "StartTime", required = true)
    @JsonProperty("StartTime")
    private String startTime;

    /**
     * 上周期长度（秒）
     * 单位：秒(s)
     */
    @NotNull(message = "上周期长度不能为空")
    @Min(value = 0, message = "周期长度不能为负数")
    @XmlElement(name = "LastCycleLen", required = true)
    @JsonProperty("LastCycleLen")
    private Integer lastCycleLen;

    /**
     * 过渡标志（本周期是否处在过渡调整）
     * 取值：0：否，1：是
     */
    @NotNull(message = "过渡标志不能为空")
    @XmlElement(name = "AdjustFlag", required = true)
    @JsonProperty("AdjustFlag")
    private Integer adjustFlag;

    // 时间格式化工具
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 过渡标志常量
    public static final Integer ADJUST_FLAG_NO = 0;  // 否：不处于过渡调整
    public static final Integer ADJUST_FLAG_YES = 1; // 是：处于过渡调整

    // 构造函数
    public CrossCycle() {
        //super();
        this.startTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        this.adjustFlag = ADJUST_FLAG_NO; // 默认不处于过渡调整
    }

    public CrossCycle(String crossId, Integer lastCycleLen) {
        //super();
        this.crossId = crossId;
        this.startTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        this.lastCycleLen = lastCycleLen;
        this.adjustFlag = ADJUST_FLAG_NO; // 默认不处于过渡调整
    }

    public CrossCycle(String crossId, String startTime, Integer lastCycleLen) {
        //super();
        this.crossId = crossId;
        this.startTime = startTime;
        this.lastCycleLen = lastCycleLen;
        this.adjustFlag = ADJUST_FLAG_NO; // 默认不处于过渡调整
    }

    public CrossCycle(String crossId, String startTime, Integer lastCycleLen, Integer adjustFlag) {
        //super();
        this.crossId = crossId;
        this.startTime = startTime;
        this.lastCycleLen = lastCycleLen;
        this.adjustFlag = adjustFlag;
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

    public Integer getAdjustFlag() {
        return adjustFlag;
    }

    public void setAdjustFlag(Integer adjustFlag) {
        this.adjustFlag = adjustFlag;
    }

    /**
     * 检查是否处于过渡调整状态
     * @return true表示处于过渡调整，false表示不处于过渡调整
     */
    public boolean isInTransition() {
        return ADJUST_FLAG_YES.equals(adjustFlag);
    }

    /**
     * 设置过渡调整状态
     * @param inTransition true表示处于过渡调整，false表示不处于过渡调整
     */
    public void setInTransition(boolean inTransition) {
        this.adjustFlag = inTransition ? ADJUST_FLAG_YES : ADJUST_FLAG_NO;
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
     * 验证过渡标志值是否有效
     * @param adjustFlag 过渡标志值
     * @return 是否为有效值（0或1）
     */
    public static boolean isValidAdjustFlag(Integer adjustFlag) {
        return adjustFlag != null &&
                (adjustFlag.equals(ADJUST_FLAG_NO) || adjustFlag.equals(ADJUST_FLAG_YES));
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

    /**
     * 验证对象完整性
     * @return 验证结果消息，null表示验证通过
     */
    public String validate() {
        if (crossId == null || crossId.trim().isEmpty()) {
            return "路口编号不能为空";
        }

        if (startTime == null || startTime.trim().isEmpty()) {
            return "周期开始时间不能为空";
        }

        if (!isValidTimeFormat(startTime)) {
            return "周期开始时间格式错误，应为yyyy-MM-dd HH:mm:ss";
        }

        if (lastCycleLen == null) {
            return "上周期长度不能为空";
        }

        if (lastCycleLen < 0) {
            return "上周期长度不能为负数";
        }

        if (!isValidAdjustFlag(adjustFlag)) {
            return "过渡标志值无效，应为0（否）或1（是）";
        }

        return null; // 验证通过
    }

    @Override
    public String toString() {
        return "CrossCycle{" +
                "crossId='" + crossId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", lastCycleLen=" + lastCycleLen +
                ", adjustFlag=" + adjustFlag +
                " (是否过渡:" + (isInTransition() ? "是" : "否") + ")" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrossCycle that = (CrossCycle) o;

        if (crossId != null ? !crossId.equals(that.crossId) : that.crossId != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (lastCycleLen != null ? !lastCycleLen.equals(that.lastCycleLen) : that.lastCycleLen != null) return false;
        return adjustFlag != null ? adjustFlag.equals(that.adjustFlag) : that.adjustFlag == null;
    }

    @Override
    public int hashCode() {
        int result = crossId != null ? crossId.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (lastCycleLen != null ? lastCycleLen.hashCode() : 0);
        result = 31 * result + (adjustFlag != null ? adjustFlag.hashCode() : 0);
        return result;
    }
}