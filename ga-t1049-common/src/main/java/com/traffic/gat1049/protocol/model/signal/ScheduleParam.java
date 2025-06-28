package com.traffic.gat1049.protocol.model.signal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.traffic.gat1049.protocol.adapters.XmlAdapter.ScheduleTypeAdapter;
import com.traffic.gat1049.protocol.model.base.BaseParam;
import com.traffic.gat1049.model.enums.ScheduleType;

import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 调度参数
 * 对应文档中的 ScheduleParam (最新版本)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "ScheduleParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScheduleParam {  //extends BaseParam

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 调度号 - 从1开始顺序取值，范围1-999，调度号单个路口中唯一
     */
    @NotNull(message = "调度号不能为空")
    @Min(value = 1, message = "调度号最小值为1")
    @Max(value = 999, message = "调度号最大值为999")
    @XmlElement(name = "ScheduleNo", required = true)
    @JsonProperty("ScheduleNo")
    private Integer scheduleNo;

    /**
     * 调度名称 - 最大长度50
     */
    @Size(max = 50, message = "调度名称最大长度为50")
    @XmlElement(name = "ScheduleName")
    @JsonProperty("ScheduleName")
    private String scheduleName;

    /**
     * 调度类型
     * 调度类型的优先级由高到低：
     * 1：特殊日调度（StartDay到EndDay标识的1天或多天）
     * 2：时间段周调度（StartDay到EndDay中的周几）
     * 3：周调度
     */
    @NotNull(message = "调度类型不能为空")
    @XmlElement(name = "Type", required = true)
    @XmlJavaTypeAdapter(ScheduleTypeAdapter.class)
    @JsonProperty("Type")
    private ScheduleType type;

    /**
     * 开始月日（MM-DD格式）
     */
    @NotBlank(message = "开始月日不能为空")
    @Pattern(regexp = "\\d{2}-\\d{2}", message = "开始月日格式错误，应为MM-DD")
    @XmlElement(name = "StartDay", required = true)
    @JsonProperty("StartDay")
    private String startDay;

    /**
     * 结束月日（MM-DD格式）
     */
    @NotBlank(message = "结束月日不能为空")
    @Pattern(regexp = "\\d{2}-\\d{2}", message = "结束月日格式错误，应为MM-DD")
    @XmlElement(name = "EndDay", required = true)
    @JsonProperty("EndDay")
    private String endDay;

    /**
     * 周几
     * 调度类型为1时无意义；
     * 调度类型为2或3时，取值为1-7分别代表周一至周日
     */
    @Min(value = 1, message = "周几最小值为1")
    @Max(value = 7, message = "周几最大值为7")
    @XmlElement(name = "WeekDay")
    @JsonProperty("WeekDay")
    private Integer weekDay;

    /**
     * 日计划号 - 取值范围1-999
     */
    @NotNull(message = "日计划号不能为空")
    @Min(value = 1, message = "日计划号最小值为1")
    @Max(value = 999, message = "日计划号最大值为999")
    @XmlElement(name = "DayPlanNo", required = true)
    @JsonProperty("DayPlanNo")
    private Integer dayPlanNo;

    // 构造函数
    public ScheduleParam() {
//        super();
    }

    public ScheduleParam(String crossId, Integer scheduleNo, ScheduleType type) {
//        super();
        this.crossId = crossId;
        this.scheduleNo = scheduleNo;
        this.type = type;
    }

    public ScheduleParam(String crossId, Integer scheduleNo, String scheduleName, ScheduleType type,
                         String startDay, String endDay, Integer dayPlanNo) {
//        super();
        this.crossId = crossId;
        this.scheduleNo = scheduleNo;
        this.scheduleName = scheduleName;
        this.type = type;
        this.startDay = startDay;
        this.endDay = endDay;
        this.dayPlanNo = dayPlanNo;
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

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
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

    /**
     * 验证调度参数的业务逻辑
     */
    public void validate() {
        // 验证日期格式
        if (startDay != null && !startDay.matches("\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("开始月日格式错误，应为MM-DD");
        }
        if (endDay != null && !endDay.matches("\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("结束月日格式错误，应为MM-DD");
        }

        // 验证调度类型相关逻辑
        if (type != null) {
            switch (type) {
                case SPECIAL_DAY:
                    // 特殊日调度时，周几字段无意义
                    break;
                case WEEK_PERIOD:
                case WEEK:
                    // 周调度时，需要指定周几
                    if (weekDay == null) {
                        throw new IllegalArgumentException("周调度类型必须指定周几");
                    }
                    break;
            }
        }
    }

    /**
     * 检查是否为工作日调度（周一到周五）
     */
    public boolean isWorkdaySchedule() {
        return weekDay != null && weekDay >= 1 && weekDay <= 5;
    }

    /**
     * 检查是否为周末调度（周六、周日）
     */
    public boolean isWeekendSchedule() {
        return weekDay != null && (weekDay == 6 || weekDay == 7);
    }

    /**
     * 获取周几的中文描述
     */
    public String getWeekDayDescription() {
        if (weekDay == null) return null;
        String[] weekDays = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return weekDay >= 1 && weekDay <= 7 ? weekDays[weekDay] : "未知";
    }

    @Override
    public String toString() {
        return "ScheduleParam{" +
                "crossId='" + crossId + '\'' +
                ", scheduleNo=" + scheduleNo +
                ", scheduleName='" + scheduleName + '\'' +
                ", type=" + (type != null ? type.getDescription() : null) +
                ", startDay='" + startDay + '\'' +
                ", endDay='" + endDay + '\'' +
                ", weekDay=" + weekDay +
                " (" + getWeekDayDescription() + ")" +
                ", dayPlanNo=" + dayPlanNo +
                "} ";    // + super.toString()
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleParam)) return false;
        ScheduleParam that = (ScheduleParam) o;
        return crossId != null && crossId.equals(that.crossId) &&
                scheduleNo != null && scheduleNo.equals(that.scheduleNo);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(crossId, scheduleNo);
    }
}