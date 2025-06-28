package com.traffic.gat1049.protocol.model.signal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 信号组参数
 * 对应文档中的 SignalGroupParam
 * 更新日期: 2025-06-28
 * 更新内容: 根据最新规范增加MaxGreen、MinGreen字段，修正字段名称和验证规则
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SignalGroupParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class SignalGroupParam {

    /**
     * 路口编号
     * 取值同表B.6中路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 信号组序号
     * 取值从1开始，范围1-999。信号组序号在单个路口中唯一
     */
    @NotNull(message = "信号组序号不能为空")
    @Min(value = 1, message = "信号组序号最小值为1")
    @Max(value = 999, message = "信号组序号最大值为999")
    @XmlElement(name = "SignalGroupNo", required = true)
    @JsonProperty("SignalGroupNo")
    private Integer signalGroupNo;

    /**
     * 信号组名称
     * 最大长度50
     */
    @Size(max = 50, message = "信号组名称最大长度为50")
    @XmlElement(name = "Name")
    @JsonProperty("Name")
    private String name;

    /**
     * 绿闪时长（秒）
     * 单位秒（s）
     */
    @Min(value = 0, message = "绿闪时长不能为负数")
    @XmlElement(name = "GreenFlashLen")
    @JsonProperty("GreenFlashLen")
    private Integer greenFlashLen;

    /**
     * 最大绿灯时长（秒）
     * 单位秒（s）
     */
    @Min(value = 0, message = "最大绿灯时长不能为负数")
    @XmlElement(name = "MaxGreen")
    @JsonProperty("MaxGreen")
    private Integer maxGreen;

    /**
     * 最小绿灯时长（秒）
     * 单位秒（s）
     */
    @Min(value = 0, message = "最小绿灯时长不能为负数")
    @XmlElement(name = "MinGreen")
    @JsonProperty("MinGreen")
    private Integer minGreen;

    /**
     * 信号灯组序号列表
     * 包含至少1个信号灯组序号
     */
    @NotEmpty(message = "信号灯组序号列表不能为空")
    @XmlElementWrapper(name = "LampGroupNoList")
    @XmlElement(name = "LampGroupNo")
    @JsonProperty("LampGroupNoList")
    private List<Integer> lampGroupNoList = new ArrayList<>();

    // 构造函数
    public SignalGroupParam() {
    }

    public SignalGroupParam(String crossId, Integer signalGroupNo) {
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

    public Integer getGreenFlashLen() {
        return greenFlashLen;
    }

    public void setGreenFlashLen(Integer greenFlashLen) {
        this.greenFlashLen = greenFlashLen;
    }

    public Integer getMaxGreen() {
        return maxGreen;
    }

    public void setMaxGreen(Integer maxGreen) {
        this.maxGreen = maxGreen;
    }

    public Integer getMinGreen() {
        return minGreen;
    }

    public void setMinGreen(Integer minGreen) {
        this.minGreen = minGreen;
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
                ", greenFlashLen=" + greenFlashLen +
                ", maxGreen=" + maxGreen +
                ", minGreen=" + minGreen +
                ", lampGroupNoList=" + lampGroupNoList +
                '}';
    }

    /**
     * 验证最大绿灯时长和最小绿灯时长的逻辑关系
     * @return 验证结果信息
     */
    public String validateGreenTimingLogic() {
        if (maxGreen != null && minGreen != null) {
            if (maxGreen < minGreen) {
                return "最大绿灯时长不能小于最小绿灯时长";
            }
        }
        return null;
    }
}