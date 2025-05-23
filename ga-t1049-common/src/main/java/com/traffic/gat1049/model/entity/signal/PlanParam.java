package com.traffic.gat1049.model.entity.signal;

/**
 * 配时方案参数
 * 对应文档中的 PlanParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "PlanParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlanParam extends BaseParam {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 方案序号 - 取值从1开始，3位数字
     */
    @NotNull(message = "方案序号不能为空")
    @Min(value = 0, message = "方案序号最小值为0")
    @Max(value = 999, message = "方案序号最大值为999")
    @XmlElement(name = "PlanNo", required = true)
    @JsonProperty("PlanNo")
    private Integer planNo;

    /**
     * 方案名称
     */
    @XmlElement(name = "PlanName")
    @JsonProperty("PlanName")
    private String planName;

    /**
     * 周期长度（秒）
     */
    @NotNull(message = "周期长度不能为空")
    @Min(value = 1, message = "周期长度最小值为1秒")
    @XmlElement(name = "CycleLen", required = true)
    @JsonProperty("CycleLen")
    private Integer cycleLen;

    /**
     * 协调相位号
     */
    @XmlElement(name = "CoordStageNo")
    @JsonProperty("CoordStageNo")
    private Integer coordStageNo;

    /**
     * 协调相位差（秒）
     */
    @XmlElement(name = "Offset")
    @JsonProperty("Offset")
    private Integer offset;

    /**
     * 阶段配时信息列表
     */
    @NotEmpty(message = "阶段配时信息列表不能为空")
    @XmlElementWrapper(name = "StageTimingList")
    @XmlElement(name = "StageTiming")
    @JsonProperty("StageTimingList")
    private List<StageTiming> stageTimingList = new ArrayList<>();

    // 构造函数
    public PlanParam() {
        super();
    }

    public PlanParam(String crossId, Integer planNo, Integer cycleLen) {
        super();
        this.crossId = crossId;
        this.planNo = planNo;
        this.cycleLen = cycleLen;
    }

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public Integer getPlanNo() {
        return planNo;
    }

    public void setPlanNo(Integer planNo) {
        this.planNo = planNo;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Integer getCycleLen() {
        return cycleLen;
    }

    public void setCycleLen(Integer cycleLen) {
        this.cycleLen = cycleLen;
    }

    public Integer getCoordStageNo() {
        return coordStageNo;
    }

    public void setCoordStageNo(Integer coordStageNo) {
        this.coordStageNo = coordStageNo;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<StageTiming> getStageTimingList() {
        return stageTimingList;
    }

    public void setStageTimingList(List<StageTiming> stageTimingList) {
        this.stageTimingList = stageTimingList;
    }

    @Override
    public String toString() {
        return "PlanParam{" +
                "crossId='" + crossId + '\'' +
                ", planNo=" + planNo +
                ", planName='" + planName + '\'' +
                ", cycleLen=" + cycleLen +
                ", coordStageNo=" + coordStageNo +
                ", offset=" + offset +
                ", stageTimingList=" + stageTimingList +
                "} " + super.toString();
    }
}