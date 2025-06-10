package com.traffic.gat1049.model.entity.intersection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.model.entity.base.BaseParam;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.PedestrianAttribute;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 人行横道参数
 * 对应文档中的 PedestrianParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "PedestrianParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class PedestrianParam extends BaseParam {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 人行横道序号 - 取值从1开始，2位数字
     */
    @NotNull(message = "人行横道序号不能为空")
    @Min(value = 1, message = "人行横道序号最小值为1")
    @Max(value = 99, message = "人行横道序号最大值为99")
    @XmlElement(name = "PedestrianNo", required = true)
    @JsonProperty("PedestrianNo")
    private Integer pedestrianNo;

    /**
     * 人行横道所在进口的方向
     */
    @XmlElement(name = "Direction", required = true)
    @JsonProperty("Direction")
    private Direction direction;

    /**
     * 人行横道属性
     */
    @XmlElement(name = "Attribute", required = true)
    @JsonProperty("Attribute")
    private PedestrianAttribute attribute;

    // 构造函数
    public PedestrianParam() {
        super();
    }

    public PedestrianParam(String crossId, Integer pedestrianNo, Direction direction) {
        super();
        this.crossId = crossId;
        this.pedestrianNo = pedestrianNo;
        this.direction = direction;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getPedestrianNo() {
        return pedestrianNo;
    }

    public void setPedestrianNo(Integer pedestrianNo) {
        this.pedestrianNo = pedestrianNo;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public PedestrianAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(PedestrianAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return "PedestrianParam{" +
                "crossId='" + crossId + '\'' +
                ", pedestrianNo=" + pedestrianNo +
                ", direction=" + direction +
                ", attribute=" + attribute +
                "} " + super.toString();
    }
}
