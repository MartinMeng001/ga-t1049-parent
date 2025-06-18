package com.traffic.gat1049.protocol.model.signal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 时段信息
 * 日计划中的时段配置
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Period {

    /**
     * 开始时间（HH:MM:SS 格式）
     */
    @NotBlank(message = "开始时间不能为空")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$",
            message = "开始时间格式必须为HH:MM:SS")
    @XmlElement(name = "StartTime", required = true)
    @JsonProperty("StartTime")
    private String startTime;

    /**
     * 配时方案号
     */
    @NotNull(message = "配时方案号不能为空")
    @Min(value = 0, message = "配时方案号最小值为0")
    @XmlElement(name = "PlanNo", required = true)
    @JsonProperty("PlanNo")
    private Integer planNo;

    /**
     * 控制方式
     */
    @NotBlank(message = "控制方式不能为空")
    @XmlElement(name = "CtrlMode", required = true)
    @JsonProperty("CtrlMode")
    private String ctrlMode;

    // 构造函数
    public Period() {}

    public Period(String startTime, Integer planNo, String ctrlMode) {
        this.startTime = startTime;
        this.planNo = planNo;
        this.ctrlMode = ctrlMode;
    }

    // Getters and Setters
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getPlanNo() {
        return planNo;
    }

    public void setPlanNo(Integer planNo) {
        this.planNo = planNo;
    }

    public String getCtrlMode() {
        return ctrlMode;
    }

    public void setCtrlMode(String ctrlMode) {
        this.ctrlMode = ctrlMode;
    }

    /**
     * 验证时间格式是否正确
     * @param time 时间字符串
     * @return 是否为有效格式
     */
    public static boolean isValidTimeFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            return false;
        }

        // 支持 HH:MM:SS 和 HH:MM 格式
        return time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?$");
    }

    /**
     * 将时间字符串标准化为 HH:MM:SS 格式
     * @param time 时间字符串（HH:MM 或 HH:MM:SS）
     * @return 标准化后的时间字符串
     */
    public static String normalizeTimeFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            return "00:00:00";
        }

        time = time.trim();

        // 如果是 HH:MM 格式，补充秒数
        if (time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            return time + ":00";
        }

        // 如果已经是 HH:MM:SS 格式，直接返回
        if (time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
            return time;
        }

        // 格式不正确，返回默认值
        return "00:00:00";
    }

    /**
     * 比较两个时间字符串的大小
     * @param time1 时间1
     * @param time2 时间2
     * @return 负数表示time1早于time2，0表示相等，正数表示time1晚于time2
     */
    public static int compareTime(String time1, String time2) {
        String normalizedTime1 = normalizeTimeFormat(time1);
        String normalizedTime2 = normalizeTimeFormat(time2);

        return normalizedTime1.compareTo(normalizedTime2);
    }

    /**
     * 将时间字符串转换为分钟数（从00:00开始计算）
     * @param time 时间字符串
     * @return 分钟数
     */
    public static int timeToMinutes(String time) {
        String normalizedTime = normalizeTimeFormat(time);
        String[] parts = normalizedTime.split(":");

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        return hours * 60 + minutes;
    }

    /**
     * 将分钟数转换为时间字符串
     * @param minutes 分钟数
     * @return 时间字符串（HH:MM:SS格式）
     */
    public static String minutesToTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;

        return String.format("%02d:%02d:00", hours, mins);
    }

    @Override
    public String toString() {
        return "Period{" +
                "startTime='" + startTime + '\'' +
                ", planNo=" + planNo +
                ", ctrlMode='" + ctrlMode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Period period = (Period) o;

        if (startTime != null ? !startTime.equals(period.startTime) : period.startTime != null) return false;
        if (planNo != null ? !planNo.equals(period.planNo) : period.planNo != null) return false;
        return ctrlMode != null ? ctrlMode.equals(period.ctrlMode) : period.ctrlMode == null;
    }

    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (planNo != null ? planNo.hashCode() : 0);
        result = 31 * result + (ctrlMode != null ? ctrlMode.hashCode() : 0);
        return result;
    }
}