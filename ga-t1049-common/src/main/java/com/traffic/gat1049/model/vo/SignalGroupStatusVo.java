package com.traffic.gat1049.model.vo;

/**
 * 信号组状态视图对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignalGroupStatusVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 信号组序号
     */
    @JsonProperty("signalGroupNo")
    private Integer signalGroupNo;

    /**
     * 信号组名称
     */
    @JsonProperty("signalGroupName")
    private String signalGroupName;

    /**
     * 当前灯态
     */
    @JsonProperty("lampStatus")
    private String lampStatus;

    /**
     * 当前灯态描述
     */
    @JsonProperty("lampStatusDesc")
    private String lampStatusDesc;

    /**
     * 剩余时间（秒）
     */
    @JsonProperty("remainingTime")
    private Integer remainingTime;

    // Getters and Setters
    public Integer getSignalGroupNo() {
        return signalGroupNo;
    }

    public void setSignalGroupNo(Integer signalGroupNo) {
        this.signalGroupNo = signalGroupNo;
    }

    public String getSignalGroupName() {
        return signalGroupName;
    }

    public void setSignalGroupName(String signalGroupName) {
        this.signalGroupName = signalGroupName;
    }

    public String getLampStatus() {
        return lampStatus;
    }

    public void setLampStatus(String lampStatus) {
        this.lampStatus = lampStatus;
    }

    public String getLampStatusDesc() {
        return lampStatusDesc;
    }

    public void setLampStatusDesc(String lampStatusDesc) {
        this.lampStatusDesc = lampStatusDesc;
    }

    public Integer getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Integer remainingTime) {
        this.remainingTime = remainingTime;
    }

    @Override
    public String toString() {
        return "SignalGroupStatusVo{" +
                "signalGroupNo=" + signalGroupNo +
                ", signalGroupName='" + signalGroupName + '\'' +
                ", lampStatus='" + lampStatus + '\'' +
                ", lampStatusDesc='" + lampStatusDesc + '\'' +
                ", remainingTime=" + remainingTime +
                '}';
    }
}
