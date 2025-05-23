package com.traffic.gat1049.model.entity.signal;

/**
 * 信号组灯态
 * 表示阶段中信号组的灯态状态
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public class SignalGroupStatus {

    /**
     * 信号组序号
     */
    @NotNull(message = "信号组序号不能为空")
    @XmlElement(name = "SignalGroupNo", required = true)
    @JsonProperty("SignalGroupNo")
    private Integer signalGroupNo;

    /**
     * 灯态
     */
    @XmlElement(name = "LampStatus", required = true)
    @JsonProperty("LampStatus")
    private LampStatus lampStatus;

    // 构造函数
    public SignalGroupStatus() {}

    public SignalGroupStatus(Integer signalGroupNo, LampStatus lampStatus) {
        this.signalGroupNo = signalGroupNo;
        this.lampStatus = lampStatus;
    }

    // Getters and Setters
    public Integer getSignalGroupNo() {
        return signalGroupNo;
    }

    public void setSignalGroupNo(Integer signalGroupNo) {
        this.signalGroupNo = signalGroupNo;
    }

    public LampStatus getLampStatus() {
        return lampStatus;
    }

    public void setLampStatus(LampStatus lampStatus) {
        this.lampStatus = lampStatus;
    }

    @Override
    public String toString() {
        return "SignalGroupStatus{" +
                "signalGroupNo=" + signalGroupNo +
                ", lampStatus=" + lampStatus +
                '}';
    }
}
