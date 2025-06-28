package com.traffic.gat1049.protocol.model.command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.InterventionTypeAdapter;
import com.traffic.gat1049.model.enums.InterventionType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 阶段干预指令
 * 对应文档中的 StageCtrl
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "AdjustStage")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdjustStage {//extends BaseCommand

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 干预的阶段号
     */
    @NotNull(message = "阶段号不能为空")
    @XmlElement(name = "StageNo", required = true)
    @JsonProperty("StageNo")
    private Integer stageNo;

    /**
     * 干预类型
     */
    @XmlElement(name = "Type", required = true)
    @XmlJavaTypeAdapter(InterventionTypeAdapter.class)
    @JsonProperty("Type")
    private InterventionType type;

    /**
     * 干预时长（秒）- 相位延长或缩短的时间
     */
    @Min(value = 1, message = "干预时长最小值为1秒")
    @XmlElement(name = "Len", required = true)
    @JsonProperty("Len")
    private Integer len;

    // 构造函数
    public AdjustStage() {
        //super();
    }

    public AdjustStage(String crossId, Integer stageNo, InterventionType type, Integer len) {
        //super();
        this.crossId = crossId;
        this.stageNo = stageNo;
        this.type = type;
        this.len = len;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getStageNo() {
        return stageNo;
    }

    public void setStageNo(Integer stageNo) {
        this.stageNo = stageNo;
    }

    public InterventionType getType() {
        return type;
    }

    public void setType(InterventionType type) {
        this.type = type;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    @Override
    public String toString() {
        return "StageCtrl{" +
                "crossId='" + crossId + '\'' +
                ", stageNo=" + stageNo +
                ", type=" + type +
                ", len=" + len +
                "} " + super.toString();
    }
}
