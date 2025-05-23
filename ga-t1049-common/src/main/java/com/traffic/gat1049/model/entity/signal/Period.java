package com.traffic.gat1049.model.entity.signal;

/**
 * 时段信息
 * 日计划中的时段配置
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class Period {

    /**
     * 开始时间（HH24:MM）
     */
    @NotNull(message = "开始时间不能为空")
    @XmlElement(name = "StartTime", required = true)
    @JsonProperty("StartTime")
    private LocalTime startTime;

    /**
     * 配时方案号
     */
    @NotNull(message = "配时方案号不能为空")
    @Min(value = 0, message = "配时方案号最小值为0")
    @XmlElement(name = "PlanNo", required = true)
    @JsonProperty("PlanNo")
    private Integer planNo;

    /**
     * 控制方式
     */
    @XmlElement(name = "CtrlMode", required = true)
    @JsonProperty("CtrlMode")
    private String ctrlMode;

    // 构造函数
    public Period() {}

    public Period(LocalTime startTime, Integer planNo, String ctrlMode) {
        this.startTime = startTime;
        this.planNo = planNo;
        this.ctrlMode = ctrlMode;
    }

    // Getters and Setters
    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Integer getPlanNo() {
        return planNo;
    }

    public void setPlanNo(Integer planNo) {
        this.planNo = planNo;
    }

    public String getCtrlMode() {
        return ctrlMode;
    }

    public void setCtrlMode(String ctrlMode) {
        this.ctrlMode = ctrlMode;
    }

    @Override
    public String toString() {
        return "Period{" +
                "startTime=" + startTime +
                ", planNo=" + planNo +
                ", ctrlMode='" + ctrlMode + '\'' +
                '}';
    }
}
