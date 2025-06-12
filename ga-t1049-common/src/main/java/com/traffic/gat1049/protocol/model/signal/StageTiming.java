package com.traffic.gat1049.protocol.model.signal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * 阶段配时信息
 * 包含阶段的时间配置和调整信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class StageTiming {

    /**
     * 阶段号
     */
    @NotNull(message = "阶段号不能为空")
    @XmlElement(name = "StageNo", required = true)
    @JsonProperty("StageNo")
    private Integer stageNo;

    /**
     * 绿灯时间（秒）
     */
    @Min(value = 0, message = "绿灯时间不能为负数")
    @XmlElement(name = "Green", required = true)
    @JsonProperty("Green")
    private Integer green;

    /**
     * 黄灯时间（秒）
     */
    @Min(value = 0, message = "黄灯时间不能为负数")
    @XmlElement(name = "Yellow", required = true)
    @JsonProperty("Yellow")
    private Integer yellow;

    /**
     * 全红时间（秒）
     */
    @Min(value = 0, message = "全红时间不能为负数")
    @XmlElement(name = "AllRed", required = true)
    @JsonProperty("AllRed")
    private Integer allRed;

    /**
     * 感应/自适应控制最大绿灯时间（秒）
     */
    @Min(value = 0, message = "最大绿灯时间不能为负数")
    @XmlElement(name = "MaxGreen")
    @JsonProperty("MaxGreen")
    private Integer maxGreen;

    /**
     * 感应/自适应控制最小绿灯时间（秒）
     */
    @Min(value = 0, message = "最小绿灯时间不能为负数")
    @XmlElement(name = "MinGreen")
    @JsonProperty("MinGreen")
    private Integer minGreen;

    /**
     * 迟开早闭配置列表
     */
    @XmlElementWrapper(name = "AdjustList")
    @XmlElement(name = "Adjust")
    @JsonProperty("AdjustList")
    private List<Adjust> adjustList = new ArrayList<>();

    // 构造函数
    public StageTiming() {}

    public StageTiming(Integer stageNo, Integer green, Integer yellow, Integer allRed) {
        this.stageNo = stageNo;
        this.green = green;
        this.yellow = yellow;
        this.allRed = allRed;
    }

    // Getters and Setters
    public Integer getStageNo() {
        return stageNo;
    }

    public void setStageNo(Integer stageNo) {
        this.stageNo = stageNo;
    }

    public Integer getGreen() {
        return green;
    }

    public void setGreen(Integer green) {
        this.green = green;
    }

    public Integer getYellow() {
        return yellow;
    }

    public void setYellow(Integer yellow) {
        this.yellow = yellow;
    }

    public Integer getAllRed() {
        return allRed;
    }

    public void setAllRed(Integer allRed) {
        this.allRed = allRed;
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

    public List<Adjust> getAdjustList() {
        return adjustList;
    }

    public void setAdjustList(List<Adjust> adjustList) {
        this.adjustList = adjustList;
    }

    @Override
    public String toString() {
        return "StageTiming{" +
                "stageNo=" + stageNo +
                ", green=" + green +
                ", yellow=" + yellow +
                ", allRed=" + allRed +
                ", maxGreen=" + maxGreen +
                ", minGreen=" + minGreen +
                ", adjustList=" + adjustList +
                '}';
    }
}
