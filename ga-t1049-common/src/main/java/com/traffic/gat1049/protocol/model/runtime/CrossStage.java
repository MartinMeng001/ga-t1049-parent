package com.traffic.gat1049.protocol.model.runtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseState;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 路口阶段
 * 对应文档中的 CrossStage
 * 符合 GA/T 1049 标准 5.2.6 路口阶段定义
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "CrossStage")
@XmlAccessorType(XmlAccessType.FIELD)
public class CrossStage {//extends BaseState

    /**
     * 路口编号
     * 取值同表B.6中路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 上个阶段号
     */
    @XmlElement(name = "LastStageNo", required = true)
    @JsonProperty("LastStageNo")
    private Integer lastStageNo;

    /**
     * 上个阶段执行时长（秒）
     */
    @Min(value = 0, message = "阶段时长不能为负数")
    @XmlElement(name = "LastStageLen", required = true)
    @JsonProperty("LastStageLen")
    private Integer lastStageLen;

    /**
     * 当前阶段号
     */
    @XmlElement(name = "CurStageNo", required = true)
    @JsonProperty("CurStageNo")
    private Integer curStageNo;

    /**
     * 当前阶段开始时间
     * 符合 GA/T 543.6 的 DE0 055405
     */
    @XmlElement(name = "CurStageStartTime", required = true)
    @JsonProperty("CurStageStartTime")
    private String curStageStartTime;

    /**
     * 当前阶段已执行时长（秒）
     */
    @Min(value = 0, message = "阶段时长不能为负数")
    @XmlElement(name = "CurStageLen", required = true)
    @JsonProperty("CurStageLen")
    private Integer curStageLen;

    // 构造函数
    public CrossStage() {
        //super();
    }

    public CrossStage(String crossId, Integer curStageNo) {
        //super();
        this.crossId = crossId;
        this.curStageNo = curStageNo;
        this.curStageLen = 0;
    }

    public CrossStage(String crossId, Integer lastStageNo, Integer lastStageLen,
                      Integer curStageNo, String curStageStartTime, Integer curStageLen) {
        //super();
        this.crossId = crossId;
        this.lastStageNo = lastStageNo;
        this.lastStageLen = lastStageLen;
        this.curStageNo = curStageNo;
        this.curStageStartTime = curStageStartTime;
        this.curStageLen = curStageLen;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getLastStageNo() {
        return lastStageNo;
    }

    public void setLastStageNo(Integer lastStageNo) {
        this.lastStageNo = lastStageNo;
    }

    public Integer getLastStageLen() {
        return lastStageLen;
    }

    public void setLastStageLen(Integer lastStageLen) {
        this.lastStageLen = lastStageLen;
    }

    public Integer getCurStageNo() {
        return curStageNo;
    }

    public void setCurStageNo(Integer curStageNo) {
        this.curStageNo = curStageNo;
    }

    public String getCurStageStartTime() {
        return curStageStartTime;
    }

    public void setCurStageStartTime(String curStageStartTime) {
        this.curStageStartTime = curStageStartTime;
    }

    public Integer getCurStageLen() {
        return curStageLen;
    }

    public void setCurStageLen(Integer curStageLen) {
        this.curStageLen = curStageLen;
    }

    @Override
    public String toString() {
        return "CrossStage{" +
                "crossId='" + crossId + '\'' +
                ", lastStageNo=" + lastStageNo +
                ", lastStageLen=" + lastStageLen +
                ", curStageNo=" + curStageNo +
                ", curStageStartTime='" + curStageStartTime + '\'' +
                ", curStageLen=" + curStageLen +
                "} ";
    }
}