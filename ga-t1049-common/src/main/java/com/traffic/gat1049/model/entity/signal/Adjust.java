package com.traffic.gat1049.model.entity.signal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.enums.AdjustOperation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * 信号组迟开早闭调整信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Adjust {

    /**
     * 调整方式
     */
    @XmlAttribute(name = "Oper")
    @JsonProperty("Oper")
    private AdjustOperation oper;

    /**
     * 信号组序号
     */
    @XmlAttribute(name = "SignalGroupNo")
    @JsonProperty("SignalGroupNo")
    private Integer signalGroupNo;

    /**
     * 调整时间（秒）
     */
    @XmlAttribute(name = "Len")
    @JsonProperty("Len")
    private Integer len;

    // 构造函数
    public Adjust() {}

    public Adjust(AdjustOperation oper, Integer signalGroupNo, Integer len) {
        this.oper = oper;
        this.signalGroupNo = signalGroupNo;
        this.len = len;
    }

    // Getters and Setters
    public AdjustOperation getOper() {
        return oper;
    }

    public void setOper(AdjustOperation oper) {
        this.oper = oper;
    }

    public Integer getSignalGroupNo() {
        return signalGroupNo;
    }

    public void setSignalGroupNo(Integer signalGroupNo) {
        this.signalGroupNo = signalGroupNo;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    @Override
    public String toString() {
        return "Adjust{" +
                "oper=" + oper +
                ", signalGroupNo=" + signalGroupNo +
                ", len=" + len +
                '}';
    }
}
