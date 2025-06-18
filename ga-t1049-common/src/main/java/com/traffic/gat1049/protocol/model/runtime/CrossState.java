package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;
import com.traffic.gat1049.model.enums.SystemState;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;

/**
 * 路口状态
 * 对应文档中的 CrossState
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossState")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossState {//extends BaseState

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 运行状态
     */
    @XmlElement(name = "Value", required = true)
    @JsonProperty("Value")
    private SystemState value;

    // 构造函数
    public CrossState() {
        //super();
    }

    public CrossState(String crossId, SystemState value) {
        //super();
        this.crossId = crossId;
        this.value = value;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public SystemState getValue() {
        return value;
    }

    public void setValue(SystemState value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CrossState{" +
                "crossId='" + crossId + '\'' +
                ", value=" + value +
                "} " + super.toString();
    }
}
