package com.traffic.gat1049.protocol.model.signal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseParam;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 日计划参数
 * 对应文档中的 DayPlanParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "DayPlanParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class DayPlanParam extends BaseParam {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 日计划号 - 取值从1开始，5位数字
     */
    @NotNull(message = "日计划号不能为空")
    @Min(value = 1, message = "日计划号最小值为1")
    @Max(value = 99999, message = "日计划号最大值为99999")
    @XmlElement(name = "DayPlanNo", required = true)
    @JsonProperty("DayPlanNo")
    private Integer dayPlanNo;

    /**
     * 时段信息列表，按照开始时间升序排列
     */
    @NotEmpty(message = "时段信息列表不能为空")
    @XmlElementWrapper(name = "PeriodList")
    @XmlElement(name = "Period")
    @JsonProperty("PeriodList")
    private List<Period> periodList = new ArrayList<>();

    // 构造函数
    public DayPlanParam() {
        super();
    }

    public DayPlanParam(String crossId, Integer dayPlanNo) {
        super();
        this.crossId = crossId;
        this.dayPlanNo = dayPlanNo;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getDayPlanNo() {
        return dayPlanNo;
    }

    public void setDayPlanNo(Integer dayPlanNo) {
        this.dayPlanNo = dayPlanNo;
    }

    public List<Period> getPeriodList() {
        return periodList;
    }

    public void setPeriodList(List<Period> periodList) {
        this.periodList = periodList;
    }

    @Override
    public String toString() {
        return "DayPlanParam{" +
                "crossId='" + crossId + '\'' +
                ", dayPlanNo=" + dayPlanNo +
                ", periodList=" + periodList +
                "} " + super.toString();
    }
}
