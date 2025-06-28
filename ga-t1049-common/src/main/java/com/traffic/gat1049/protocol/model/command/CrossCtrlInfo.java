package com.traffic.gat1049.protocol.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.ControlModeAdapter;
import com.traffic.gat1049.model.enums.ControlMode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 路口控制方式方案命令
 * 对应文档中的 CrossCtrlInfo
 *
 * 用途：
 * 1. 路口控制方式方案状态推送（5.2.4）
 * 2. 指定控制方式方案命令（5.3.2）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossCtrlInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossCtrlInfo {

    /**
     * 路口编号
     * 取值同表B.6中路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 控制方式
     * 取值按表B.21要求，使用适配器进行XML序列化
     */
    @NotNull(message = "控制方式不能为空")
    @XmlElement(name = "ControlMode", required = true)
    @XmlJavaTypeAdapter(ControlModeAdapter.class)
    @JsonProperty("ControlMode")
    private ControlMode controlMode;

    /**
     * 方案序号
     * 当控制方式为00、11、12、13时取值0（表示无方案）
     * 取值范围：0-9999
     */
    @Min(value = 0, message = "方案序号不能小于0")
    @Max(value = 9999, message = "方案序号不能大于9999")
    @XmlElement(name = "PlanNo", required = true)
    @JsonProperty("PlanNo")
    private Integer planNo;

    /**
     * 路口本地时间
     * 符合GA/T 543.6的DE0554
     *
     * 使用场景：
     * - 主动推送时：为路口发出推送信息的时间
     * - 请求应答时：为路口应答请求信息的时间
     * - 指定控制方式方案命令中：该时间无意义
     */
    @XmlElement(name = "Time", required = true)
    @JsonProperty("Time")
    private String time;

    // 构造函数
    public CrossCtrlInfo() {
    }

    public CrossCtrlInfo(String crossId, ControlMode controlMode, Integer planNo) {
        this.crossId = crossId;
        this.controlMode = controlMode;
        this.planNo = planNo;
        this.time = getCurrentTimeString();
    }

    public CrossCtrlInfo(String crossId, ControlMode controlMode, Integer planNo, String time) {
        this.crossId = crossId;
        this.controlMode = controlMode;
        this.planNo = planNo;
        this.time = time;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public ControlMode getControlMode() {
        return controlMode;
    }

    public void setControlMode(ControlMode controlMode) {
        this.controlMode = controlMode;
        // 自动调整方案号 - 特殊控制模式方案号为0
        if (isSpecialControlMode(controlMode)) {
            this.planNo = 0;
        }
    }

    public Integer getPlanNo() {
        return planNo;
    }

    public void setPlanNo(Integer planNo) {
        this.planNo = planNo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 检查是否为特殊控制模式（无需方案号）
     * 对应控制方式：00、11、12、13
     */
    private boolean isSpecialControlMode(ControlMode mode) {
        return mode == ControlMode.CANCEL ||
                mode == ControlMode.LIGHT_OFF ||
                mode == ControlMode.ALL_RED ||
                mode == ControlMode.ALL_YELLOW_FLASH;
    }

    /**
     * 获取当前时间字符串
     * 格式符合GA/T 543.6的DE0554要求
     */
    private String getCurrentTimeString() {
        return java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 验证控制方式方案的有效性
     */
    public boolean isValid() {
        if (crossId == null || crossId.trim().isEmpty()) {
            return false;
        }
        if (controlMode == null) {
            return false;
        }
        if (planNo == null || planNo < 0 || planNo > 9999) {
            return false;
        }

        // 特殊控制模式必须方案号为0
        if (isSpecialControlMode(controlMode) && planNo != 0) {
            return false;
        }

        return true;
    }

    /**
     * 创建用于状态推送的CrossCtrlInfo
     */
    public static CrossCtrlInfo forStatusPush(String crossId, ControlMode controlMode, Integer planNo) {
        CrossCtrlInfo info = new CrossCtrlInfo(crossId, controlMode, planNo);
        info.setTime(info.getCurrentTimeString());
        return info;
    }

    /**
     * 创建用于命令下发的CrossCtrlInfo（时间字段无意义）
     */
    public static CrossCtrlInfo forCommand(String crossId, ControlMode controlMode, Integer planNo) {
        CrossCtrlInfo info = new CrossCtrlInfo(crossId, controlMode, planNo);
        info.setTime(""); // 命令中时间无意义
        return info;
    }

    @Override
    public String toString() {
        return "CrossCtrlInfo{" +
                "crossId='" + crossId + '\'' +
                ", controlMode=" + controlMode +
                ", planNo=" + planNo +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrossCtrlInfo that = (CrossCtrlInfo) o;

        if (!crossId.equals(that.crossId)) return false;
        if (controlMode != that.controlMode) return false;
        return planNo.equals(that.planNo);
    }

    @Override
    public int hashCode() {
        int result = crossId.hashCode();
        result = 31 * result + controlMode.hashCode();
        result = 31 * result + planNo.hashCode();
        return result;
    }
}