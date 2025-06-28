package com.traffic.gat1049.protocol.model.system;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.SystemStateAdapter;
import com.traffic.gat1049.protocol.model.base.LocalDateTimeAdapter;
import com.traffic.gat1049.model.enums.SystemState;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 系统状态
 * 对应文档中的 SysState
 *
 * 表B.23系统状态定义：
 * 1. 系统运行状态(Value) - 数值类型，取值按表B.24要求
 * 2. 系统当前时间(Time) - 字符类型，符合GA/T 543.6的DE00554
 *
 * 表B.24状态取值表：
 * 1. Online  - 正常在线
 * 2. Offline - 脱机、断线
 * 3. Error   - 异常故障
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SysState")
@XmlAccessorType(XmlAccessType.FIELD)
public class SysState {

    /**
     * 系统运行状态
     * 取值按表B.24要求：Online、Offline、Error
     */
    @NotNull(message = "系统运行状态不能为空")
    @XmlElement(name = "Value", required = true)
    @XmlJavaTypeAdapter(SystemStateAdapter.class)
    @JsonProperty("Value")
    private SystemState value;

    /**
     * 系统当前时间
     * 符合GA/T 543.6的DE00554标准
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @NotNull(message = "系统当前时间不能为空")
    @XmlElement(name = "Time", required = true)
    @JsonProperty("Time")
    private String time;

    // 时间格式化工具
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 构造函数
    public SysState() {
        this.time = getCurrentTimeString();
    }

    public SysState(SystemState value) {
        this.value = value;
        this.time = getCurrentTimeString();
    }

    public SysState(SystemState value, String time) {
        this.value = value;
        this.time = time;
    }

    public SysState(SystemState value, LocalDateTime dateTime) {
        this.value = value;
        this.time = dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : getCurrentTimeString();
    }

    // 辅助方法：获取当前时间字符串
    private String getCurrentTimeString() {
        return LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }

    // 辅助方法：将字符串时间转换为LocalDateTime
    public LocalDateTime getTimeAsLocalDateTime() {
        if (time == null || time.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(time, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    // 辅助方法：从LocalDateTime设置时间
    public void setTimeFromLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            this.time = dateTime.format(DATE_TIME_FORMATTER);
        }
    }

    // Getters and Setters
    public SystemState getValue() {
        return value;
    }

    public void setValue(SystemState value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 更新系统状态和时间
     * @param newState 新的系统状态
     */
    public void updateState(SystemState newState) {
        this.value = newState;
        this.time = getCurrentTimeString();
    }

    /**
     * 验证时间格式是否正确
     * @return 是否为有效的时间格式
     */
    public boolean isValidTimeFormat() {
        if (time == null || time.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDateTime.parse(time, DATE_TIME_FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查系统是否在线
     * @return 系统是否正常在线
     */
    public boolean isOnline() {
        return SystemState.ONLINE.equals(value);
    }

    /**
     * 检查系统是否离线
     * @return 系统是否脱机断线
     */
    public boolean isOffline() {
        return SystemState.OFFLINE.equals(value);
    }

    /**
     * 检查系统是否异常
     * @return 系统是否异常故障
     */
    public boolean hasError() {
        return SystemState.ERROR.equals(value);
    }

    @Override
    public String toString() {
        return "SysState{" +
                "value=" + value +
                ", time='" + time + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SysState sysState = (SysState) o;

        if (value != sysState.value) return false;
        return time != null ? time.equals(sysState.time) : sysState.time == null;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}