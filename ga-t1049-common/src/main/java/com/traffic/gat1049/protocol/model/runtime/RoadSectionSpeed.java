package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 路段推荐车速
 * 线路中路段的推荐车速信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class RoadSectionSpeed {

    /**
     * 上游路口编号
     */
    @NotBlank(message = "上游路口编号不能为空")
    @XmlElement(name = "UpCrossID", required = true)
    @JsonProperty("UpCrossID")
    private String upCrossId;

    /**
     * 下游路口编号
     */
    @NotBlank(message = "下游路口编号不能为空")
    @XmlElement(name = "DownCrossID", required = true)
    @JsonProperty("DownCrossID")
    private String downCrossId;

    /**
     * 推荐车速（公里/小时）
     */
    @Min(value = 0, message = "推荐车速不能为负数")
    @XmlElement(name = "RecommendSpeed", required = true)
    @JsonProperty("RecommendSpeed")
    private Integer recommendSpeed;

    // 构造函数
    public RoadSectionSpeed() {}

    public RoadSectionSpeed(String upCrossId, String downCrossId, Integer recommendSpeed) {
        this.upCrossId = upCrossId;
        this.downCrossId = downCrossId;
        this.recommendSpeed = recommendSpeed;
    }

    // Getters and Setters
    public String getUpCrossId() {
        return upCrossId;
    }

    public void setUpCrossId(String upCrossId) {
        this.upCrossId = upCrossId;
    }

    public String getDownCrossId() {
        return downCrossId;
    }

    public void setDownCrossId(String downCrossId) {
        this.downCrossId = downCrossId;
    }

    public Integer getRecommendSpeed() {
        return recommendSpeed;
    }

    public void setRecommendSpeed(Integer recommendSpeed) {
        this.recommendSpeed = recommendSpeed;
    }

    @Override
    public String toString() {
        return "RoadSectionSpeed{" +
                "upCrossId='" + upCrossId + '\'' +
                ", downCrossId='" + downCrossId + '\'' +
                ", recommendSpeed=" + recommendSpeed +
                '}';
    }
}
