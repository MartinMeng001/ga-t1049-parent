package com.traffic.gat1049.protocol.model.signal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 信号组灯色状态
 * 对应GA/T 1049.2标准中的 SignalGroupStatus
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class SignalGroupStatus {

    /**
     * 信号组序号
     */
    @NotNull(message = "信号组序号不能为空")
    @XmlElement(name = "SignalGroupNo", required = true)
    @JsonProperty("SignalGroupNo")
    private Integer signalGroupNo;

    /**
     * 灯色状态
     * 长度3。当信号灯组类型为61、62、63时（有轨电车专用信号），
     * 自左向右每位字符分别表示禁止通行、过渡、通行信号灯色状态；
     * 其他信号灯组类型，自左向右每位字符分别表示红色、黄色、绿色信号灯色状态。
     * 每位字符代表的具体灯色状态取值按表B.15要求：
     * 0-无灯，1-灭灯，2-亮灯，3-闪灯
     */
    @XmlElement(name = "LampStatus", required = true)
    @JsonProperty("LampStatus")
    private String lampStatus;

    /**
     * 剩余时长（秒）
     * 表示当前灯色状态的剩余时间
     */
    @XmlElement(name = "RemainTime")
    @JsonProperty("RemainTime")
    private Integer remainTime;

    // 构造函数
    public SignalGroupStatus() {}

    public SignalGroupStatus(Integer signalGroupNo, String lampStatus) {
        this.signalGroupNo = signalGroupNo;
        this.lampStatus = lampStatus;
    }

    public SignalGroupStatus(Integer signalGroupNo, String lampStatus, Integer remainTime) {
        this.signalGroupNo = signalGroupNo;
        this.lampStatus = lampStatus;
        this.remainTime = remainTime;
    }

    // Getters and Setters
    public Integer getSignalGroupNo() {
        return signalGroupNo;
    }

    public void setSignalGroupNo(Integer signalGroupNo) {
        this.signalGroupNo = signalGroupNo;
    }

    public String getLampStatus() {
        return lampStatus;
    }

    public void setLampStatus(String lampStatus) {
        this.lampStatus = lampStatus;
    }

    public Integer getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(Integer remainTime) {
        this.remainTime = remainTime;
    }

    @Override
    public String toString() {
        return "SignalGroupStatus{" +
                "signalGroupNo=" + signalGroupNo +
                ", lampStatus='" + lampStatus + '\'' +
                ", remainTime=" + remainTime +
                '}';
    }
}