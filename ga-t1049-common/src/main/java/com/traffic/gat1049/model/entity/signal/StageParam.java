package com.traffic.gat1049.model.entity.signal;

/**
 * 阶段参数
 * 对应文档中的 StageParam
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "StageParam")
@XmlAccessorType(XmlAccessType.FIELD)
public class StageParam extends BaseParam {

    /**
     * 路口编号
     */
    @NotBlank(message = "路口编号不能为空")
    @XmlElement(name = "CrossID", required = true)
    @JsonProperty("CrossID")
    private String crossId;

    /**
     * 阶段号
     * 阶段在路口中唯一时，取值从1开始顺序编号
     * 阶段在方案中唯一时，取值为方案序号×100+阶段顺序号
     */
    @NotNull(message = "阶段号不能为空")
    @Min(value = 1, message = "阶段号最小值为1")
    @XmlElement(name = "StageNo", required = true)
    @JsonProperty("StageNo")
    private Integer stageNo;

    /**
     * 阶段名称
     */
    @XmlElement(name = "StageName")
    @JsonProperty("StageName")
    private String stageName;

    /**
     * 特征 - 0：一般，1：感应
     */
    @XmlElement(name = "Attribute")
    @JsonProperty("Attribute")
    private Integer attribute;

    /**
     * 信号组灯态列表
     */
    @NotEmpty(message = "信号组灯态列表不能为空")
    @XmlElementWrapper(name = "SignalGroupStatusList")
    @XmlElement(name = "SignalGroupStatus")
    @JsonProperty("SignalGroupStatusList")
    private List<SignalGroupStatus> signalGroupStatusList = new ArrayList<>();

    // 构造函数
    public StageParam() {
        super();
    }

    public StageParam(String crossId, Integer stageNo) {
        super();
        this.crossId = crossId;
        this.stageNo = stageNo;
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

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public Integer getAttribute() {
        return attribute;
    }

    public void setAttribute(Integer attribute) {
        this.attribute = attribute;
    }

    public List<SignalGroupStatus> getSignalGroupStatusList() {
        return signalGroupStatusList;
    }

    public void setSignalGroupStatusList(List<SignalGroupStatus> signalGroupStatusList) {
        this.signalGroupStatusList = signalGroupStatusList;
    }

    @Override
    public String toString() {
        return "StageParam{" +
                "crossId='" + crossId + '\'' +
                ", stageNo=" + stageNo +
                ", stageName='" + stageName + '\'' +
                ", attribute=" + attribute +
                ", signalGroupStatusList=" + signalGroupStatusList +
                "} " + super.toString();
    }
}
