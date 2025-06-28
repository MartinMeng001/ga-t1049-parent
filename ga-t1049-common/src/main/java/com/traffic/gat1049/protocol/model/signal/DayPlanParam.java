// ===============================================
// 更新的 Period.java - 时段信息类
// ===============================================

package com.traffic.gat1049.protocol.model.signal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.model.base.BaseParam;

import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 日计划参数
 * 对应文档中的 DayPlanParam
 * 更新版本 - 符合最新协议定义
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "DayPlanParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class DayPlanParam extends BaseParam {

    /**
     * 路口编号
     * 取值同表B.6中路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 日计划号
     * 从1开始顺序取值，范围1-999
     * 日计划号在单个路口中唯一
     */
    @NotNull(message = "日计划号不能为空")
    @Min(value = 1, message = "日计划号最小值为1")
    @Max(value = 999, message = "日计划号最大值为999")
    @XmlElement(name = "DayPlanNo", required = true)
    @JsonProperty("DayPlanNo")
    private Integer dayPlanNo;

    /**
     * 日计划名称
     * 最大长度50
     */
    @Size(max = 50, message = "日计划名称最大长度为50")
    @XmlElement(name = "DayPlanName")
    @JsonProperty("DayPlanName")
    private String dayPlanName;

    /**
     * 时段信息列表
     * 包含至少1个时段信息，按照开始时间升序排列
     */
    @NotEmpty(message = "时段信息列表不能为空")
    @Valid
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

    public DayPlanParam(String crossId, Integer dayPlanNo, String dayPlanName) {
        super();
        this.crossId = crossId;
        this.dayPlanNo = dayPlanNo;
        this.dayPlanName = dayPlanName;
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

    public String getDayPlanName() {
        return dayPlanName;
    }

    public void setDayPlanName(String dayPlanName) {
        this.dayPlanName = dayPlanName;
    }

    public List<Period> getPeriodList() {
        return periodList;
    }

    public void setPeriodList(List<Period> periodList) {
        this.periodList = periodList;
    }

    /**
     * 添加时段信息
     * @param period 时段信息
     */
    public void addPeriod(Period period) {
        if (this.periodList == null) {
            this.periodList = new ArrayList<>();
        }
        this.periodList.add(period);
    }

    /**
     * 验证日计划参数的有效性
     * @return 验证结果
     */
    public boolean isValid() {
        if (crossId == null || crossId.trim().isEmpty()) {
            return false;
        }
        if (dayPlanNo == null || dayPlanNo < 1 || dayPlanNo > 999) {
            return false;
        }
        if (dayPlanName != null && dayPlanName.length() > 50) {
            return false;
        }
        if (periodList == null || periodList.isEmpty()) {
            return false;
        }

        // 验证时段列表是否按时间升序排列
        for (int i = 0; i < periodList.size() - 1; i++) {
            Period current = periodList.get(i);
            Period next = periodList.get(i + 1);
            if (Period.compareTime(current.getStartTime(), next.getStartTime()) >= 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * 对时段列表按开始时间排序
     */
    public void sortPeriodsByTime() {
        if (periodList != null && periodList.size() > 1) {
            periodList.sort((p1, p2) -> Period.compareTime(p1.getStartTime(), p2.getStartTime()));
        }
    }

    @Override
    public String toString() {
        return "DayPlanParam{" +
                "crossId='" + crossId + '\'' +
                ", dayPlanNo=" + dayPlanNo +
                ", dayPlanName='" + dayPlanName + '\'' +
                ", periodList=" + periodList +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayPlanParam that = (DayPlanParam) o;

        if (crossId != null ? !crossId.equals(that.crossId) : that.crossId != null) return false;
        if (dayPlanNo != null ? !dayPlanNo.equals(that.dayPlanNo) : that.dayPlanNo != null) return false;
        if (dayPlanName != null ? !dayPlanName.equals(that.dayPlanName) : that.dayPlanName != null) return false;
        return periodList != null ? periodList.equals(that.periodList) : that.periodList == null;
    }

    @Override
    public int hashCode() {
        int result = crossId != null ? crossId.hashCode() : 0;
        result = 31 * result + (dayPlanNo != null ? dayPlanNo.hashCode() : 0);
        result = 31 * result + (dayPlanName != null ? dayPlanName.hashCode() : 0);
        result = 31 * result + (periodList != null ? periodList.hashCode() : 0);
        return result;
    }
}