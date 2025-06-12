package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;
import com.traffic.gat1049.protocol.model.base.LocalDateTimeAdapter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

/**
 * 路口周期
 * 对应文档中的 CrossCycle
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossCycle")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossCycle extends BaseState {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 周期开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "StartTime", required = true)
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    @JsonProperty("StartTime")
    private LocalDateTime startTime;

    /**
     * 上周期长度（秒）
     */
    @Min(value = 0, message = "周期长度不能为负数")
    @XmlElement(name = "LastCycleLen", required = true)
    @JsonProperty("LastCycleLen")
    private Integer lastCycleLen;

    // 构造函数
    public CrossCycle() {
        super();
        this.startTime = LocalDateTime.now();
    }

    public CrossCycle(String crossId, Integer lastCycleLen) {
        super();
        this.crossId = crossId;
        this.startTime = LocalDateTime.now();
        this.lastCycleLen = lastCycleLen;
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

    public Integer getLastCycleLen() {
        return lastCycleLen;
    }

    public void setLastCycleLen(Integer lastCycleLen) {
        this.lastCycleLen = lastCycleLen;
    }

    @Override
    public String toString() {
        return "CrossCycle{" +
                "crossId='" + crossId + '\'' +
                ", startTime=" + startTime +
                ", lastCycleLen=" + lastCycleLen +
                "} " + super.toString();
    }
}
