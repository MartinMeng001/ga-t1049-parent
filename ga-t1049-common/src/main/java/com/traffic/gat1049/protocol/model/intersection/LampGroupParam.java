package com.traffic.gat1049.protocol.model.intersection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.DirectionAdapter;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.LampGroupTypeAdapter;
import com.traffic.gat1049.model.enums.Direction;
import com.traffic.gat1049.model.enums.LampGroupType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 信号灯组参数
 * 对应文档中的 LampGroup
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "LampGroupParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class LampGroupParam {//extends BaseParam

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 信号灯组序号 - 取值从1开始，2位数字
     */
    @NotNull(message = "信号灯组序号不能为空")
    @Min(value = 1, message = "信号灯组序号最小值为1")
    @Max(value = 99, message = "信号灯组序号最大值为99")
    @XmlElement(name = "LampGroupNo", required = true)
    @JsonProperty("LampGroupNo")
    private Integer lampGroupNo;

    /**
     * 信号灯组控制的进口方向
     */
    @XmlElement(name = "Direction", required = true)
    @XmlJavaTypeAdapter(DirectionAdapter.class)
    @JsonProperty("Direction")
    private Direction direction;

    /**
     * 信号灯组类型
     */
    @XmlElement(name = "Type", required = true)
    @XmlJavaTypeAdapter(LampGroupTypeAdapter.class)
    @JsonProperty("Type")
    private LampGroupType type;

    // 构造函数
    public LampGroupParam() {
        //super();
    }

    public LampGroupParam(String crossId, Integer lampGroupNo, Direction direction, LampGroupType type) {
        //super();
        this.crossId = crossId;
        this.lampGroupNo = lampGroupNo;
        this.direction = direction;
        this.type = type;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getLampGroupNo() {
        return lampGroupNo;
    }

    public void setLampGroupNo(Integer lampGroupNo) {
        this.lampGroupNo = lampGroupNo;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public LampGroupType getType() {
        return type;
    }

    public void setType(LampGroupType type) {
        this.type = type;
    }
    /**
     * 判断当前信号灯组参数的进口方向和信号灯组类型是否与传入的参数相等。
     *
     * @param otherDirection 传入的进口方向
     * @param otherType 传入的信号灯组类型
     * @return 如果direction和type都相等则返回true，否则返回false。
     */
    public boolean matchesDirectionAndType(Direction otherDirection, LampGroupType otherType) {
        // 使用 Objects.equals 避免 NullPointerException，即使其中一个或两个参数为 null 也能正确比较
        return java.util.Objects.equals(this.direction, otherDirection) &&
                java.util.Objects.equals(this.type, otherType);
    }
    @Override
    public String toString() {
        return "LampGroup{" +
                "crossId='" + crossId + '\'' +
                ", lampGroupNo=" + lampGroupNo +
                ", direction=" + direction +
                ", type=" + type +
                "} " + super.toString();
    }
}
