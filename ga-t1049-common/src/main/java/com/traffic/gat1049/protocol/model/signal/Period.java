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
 * 更新版本 - 符合最新协议定义
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Period {

    /**
     * 开始时间（HH24:MM 格式）
     * 根据最新协议，时间格式为 HH24:MM
     */
    @NotBlank(message = "开始时间不能为空")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$",
            message = "开始时间格式必须为HH24:MM")
    @XmlElement(name = "StartTime", required = true)
    @JsonProperty("StartTime")
    private String startTime;

    /**
     * 配时方案序号
     * 取值1-9999
     * 当方案序号大于0时，表示路口有对应的配时方案
     * 等于0时，表示路口使用无方案的特殊控制（例如黄闪、关灯、全红等）
     */
    @NotNull(message = "配时方案序号不能为空")
    @Min(value = 0, message = "配时方案序号最小值为0")
    @XmlElement(name = "PlanNo", required = true)
    @JsonProperty("PlanNo")
    private Integer planNo;

    /**
     * 控制方式
     * 取值按表B.21要求
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
     * 验证时间格式是否正确（HH24:MM）
     * @param time 时间字符串
     * @return 是否为有效格式
     */
    public static boolean isValidTimeFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            return false;
        }
        // 只支持 HH24:MM 格式
        return time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    /**
     * 将时间字符串标准化为 HH24:MM 格式
     * @param time 时间字符串
     * @return 标准化后的时间字符串
     */
    public static String normalizeTimeFormat(String time) {
        if (time == null || time.trim().isEmpty()) {
            return "00:00";
        }

        time = time.trim();

        // 如果是 HH:MM:SS 格式，提取 HH:MM 部分
        if (time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
            return time.substring(0, 5);
        }

        // 如果已经是 HH:MM 格式，直接返回
        if (time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            return time;
        }

        // 如果格式不正确，返回默认值
        return "00:00";
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
     * @return 时间字符串（HH24:MM格式）
     */
    public static String minutesToTime(int minutes) {
        int hours = (minutes / 60) % 24;
        int mins = minutes % 60;

        return String.format("%02d:%02d", hours, mins);
    }

    /**
     * 验证控制方式是否有效
     * 根据表B.21控制方式取值表
     * @param ctrlMode 控制方式
     * @return 是否有效
     */
    public static boolean isValidCtrlMode(String ctrlMode) {
        if (ctrlMode == null || ctrlMode.trim().isEmpty()) {
            return false;
        }

        // 根据表B.21的取值范围
        String[] validModes = {
                "00", "01", "11", "12", "13",
                "21", "22", "23",
                "31", "32", "33",
                "41",
                "51", "52", "53"
        };

        for (String validMode : validModes) {
            if (validMode.equals(ctrlMode)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取控制方式的描述
     * @param ctrlMode 控制方式代码
     * @return 控制方式描述
     */
    public static String getCtrlModeDescription(String ctrlMode) {
        if (ctrlMode == null) return "未知";

        switch (ctrlMode) {
            case "00": return "撤销或恢复自主";
            case "01": return "本地手动控制";
            case "11": return "特殊控制-全部关灯";
            case "12": return "特殊控制-全红";
            case "13": return "特殊控制-全部黄闪";
            case "21": return "单点多时段定时控制";
            case "22": return "单点感应控制";
            case "23": return "单点自适应控制";
            case "31": return "线协调定时控制";
            case "32": return "线协调感应控制";
            case "33": return "线协调自适应控制";
            case "41": return "区域协调控制";
            case "51": return "干预控制-手动控制";
            case "52": return "干预控制-锁定阶段";
            case "53": return "干预控制-指定方案";
            default: return "预留控制方式(" + ctrlMode + ")";
        }
    }

    /**
     * 验证时段信息的完整性
     * @return 验证结果
     */
    public boolean isValid() {
        return isValidTimeFormat(startTime) &&
                planNo != null && planNo >= 0 && planNo <= 9999 &&
                isValidCtrlMode(ctrlMode);
    }

    @Override
    public String toString() {
        return "Period{" +
                "startTime='" + startTime + '\'' +
                ", planNo=" + planNo +
                ", ctrlMode='" + ctrlMode + '\'' +
                " (" + getCtrlModeDescription(ctrlMode) + ")" +
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