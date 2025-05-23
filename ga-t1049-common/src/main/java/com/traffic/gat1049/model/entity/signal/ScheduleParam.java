package com.traffic.gat1049.model.entity.signal;

/**
 * 调度参数
 * 对应文档中的 ScheduleParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "ScheduleParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScheduleParam extends BaseParam {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 调度号 - 取值从1开始，3位数字
     */
    @NotNull(message = "调度号不能为空")
    @Min(value = 1, message = "调度号最小值为1")
    @Max(value = 999, message = "调度号最大值为999")
    @XmlElement(name = "ScheduleNo", required = true)
    @JsonProperty("ScheduleNo")
    private Integer scheduleNo;

    /**
     * 调度类型
     */
    @XmlElement(name = "Type", required = true)
    @JsonProperty("Type")
    private ScheduleType type;

    /**
     * 开始月日（MM-DD）
     */
    @Pattern(regexp = "\\d{2}-\\d{2}", message = "开始月日格式错误，应为MM-DD")
    @XmlElement(name = "StartDay", required = true)
    @JsonProperty("StartDay")
    private String startDay;

    /**
     * 结束月日（MM-DD）
     */
    @Pattern(regexp = "\\d{2}-\\d{2}", message = "结束月日格式错误，应为MM-DD")
    @XmlElement(name = "EndDay", required = true)
    @JsonProperty("EndDay")
    private String endDay;

    /**
     * 周几 - 调度类型为周调度时有效，取值为1-7分别代表周一至周日
     */
    @Min(value = 1, message = "周几最小值为1")
    @Max(value = 7, message = "周几最大值为7")
    @XmlElement(name = "WeekDay")
    @JsonProperty("WeekDay")
    private Integer weekDay;

    /**
     * 日计划号
     */
    @NotNull(message = "日计划号不能为空")
    @XmlElement(name = "DayPlanNo", required = true)
    @JsonProperty("DayPlanNo")
    private Integer dayPlanNo;

    // 构造函数
    public ScheduleParam() {
        super();
    }

    public ScheduleParam(String crossId, Integer scheduleNo, ScheduleType type) {
        super();
        this.crossId = crossId;
        this.scheduleNo = scheduleNo;
        this.type = type;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getScheduleNo() {
        return scheduleNo;
    }

    public void setScheduleNo(Integer scheduleNo) {
        this.scheduleNo = scheduleNo;
    }

    public ScheduleType getType() {
        return type;
    }

    public void setType(ScheduleType type) {
        this.type = type;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public Integer getDayPlanNo() {
        return dayPlanNo;
    }

    public void setDayPlanNo(Integer dayPlanNo) {
        this.dayPlanNo = dayPlanNo;
    }

    @Override
    public String toString() {
        return "ScheduleParam{" +
                "crossId='" + crossId + '\'' +
                ", scheduleNo=" + scheduleNo +
                ", type=" + type +
                ", startDay='" + startDay + '\'' +
                ", endDay='" + endDay + '\'' +
                ", weekDay=" + weekDay +
                ", dayPlanNo=" + dayPlanNo +
                "} " + super.toString();
    }
}
