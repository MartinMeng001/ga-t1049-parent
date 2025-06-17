package com.traffic.gat1049.protocol.model.signal;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseParam;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 信号组参数
 * 对应文档中的 SignalGroupParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SignalGroupParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class SignalGroupParam {//extends BaseParam

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 信号组序号 - 取值从1开始，2位数字
     */
    @NotNull(message = "信号组序号不能为空")
    @Min(value = 1, message = "信号组序号最小值为1")
    @Max(value = 99, message = "信号组序号最大值为99")
    @XmlElement(name = "SignalGroupNo", required = true)
    @JsonProperty("SignalGroupNo")
    private Integer signalGroupNo;

    /**
     * 信号组名称
     */
    @XmlElement(name = "Name")
    @JsonProperty("Name")
    private String name;

    /**
     * 绿闪时长（秒）
     */
    @Min(value = 0, message = "绿闪时长不能为负数")
    @XmlElement(name = "GreenFlushLen")
    @JsonProperty("GreenFlushLen")
    private Integer greenFlushLen;

    /**
     * 信号灯组序号列表
     */
    @NotEmpty(message = "信号灯组序号列表不能为空")
    @XmlElementWrapper(name = "LampGroupNoList")
    @XmlElement(name = "LampGroupNo")
    @JsonProperty("LampGroupNoList")
    private List<Integer> lampGroupNoList = new ArrayList<>();

    // 构造函数
    public SignalGroupParam() {
        super();
    }

    public SignalGroupParam(String crossId, Integer signalGroupNo) {
        super();
        this.crossId = crossId;
        this.signalGroupNo = signalGroupNo;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getSignalGroupNo() {
        return signalGroupNo;
    }

    public void setSignalGroupNo(Integer signalGroupNo) {
        this.signalGroupNo = signalGroupNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGreenFlushLen() {
        return greenFlushLen;
    }

    public void setGreenFlushLen(Integer greenFlushLen) {
        this.greenFlushLen = greenFlushLen;
    }

    public List<Integer> getLampGroupNoList() {
        return lampGroupNoList;
    }

    public void setLampGroupNoList(List<Integer> lampGroupNoList) {
        this.lampGroupNoList = lampGroupNoList;
    }

    @Override
    public String toString() {
        return "SignalGroupParam{" +
                "crossId='" + crossId + '\'' +
                ", signalGroupNo=" + signalGroupNo +
                ", name='" + name + '\'' +
                ", greenFlushLen=" + greenFlushLen +
                ", lampGroupNoList=" + lampGroupNoList +
                "} " + super.toString();
    }
}
