package com.traffic.gat1049.model.entity.runtime;

/**
 * 信号机故障
 * 对应文档中的 SignalControllerError
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SignalControllerError")
@XmlAccessorType(XmlAccessType.FIELD)
public class SignalControllerError extends BaseState {

    /**
     * 信号机编号
     */
    @NotBlank(message = "信号机编号不能为空")
    @XmlElement(name = "SignalControlerID", required = true)
    @JsonProperty("SignalControlerID")
    private String signalControllerId;

    /**
     * 故障类型
     */
    @XmlElement(name = "ErrorType", required = true)
    @JsonProperty("ErrorType")
    private ControllerErrorType errorType;

    /**
     * 故障描述
     */
    @XmlElement(name = "ErrorDesc")
    @JsonProperty("ErrorDesc")
    private String errorDesc;

    /**
     * 故障发生时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "OccurTime", required = true)
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    @JsonProperty("OccurTime")
    private LocalDateTime occurTime;

    // 构造函数
    public SignalControllerError() {
        super();
        this.occurTime = LocalDateTime.now();
    }

    public SignalControllerError(String signalControllerId, ControllerErrorType errorType) {
        super();
        this.signalControllerId = signalControllerId;
        this.errorType = errorType;
        this.occurTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getSignalControllerId() {
        return signalControllerId;
    }

    public void setSignalControllerId(String signalControllerId) {
        this.signalControllerId = signalControllerId;
    }

    public ControllerErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ControllerErrorType errorType) {
        this.errorType = errorType;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public LocalDateTime getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(LocalDateTime occurTime) {
        this.occurTime = occurTime;
    }

    @Override
    public String toString() {
        return "SignalControllerError{" +
                "signalControllerId='" + signalControllerId + '\'' +
                ", errorType=" + errorType +
                ", errorDesc='" + errorDesc + '\'' +
                ", occurTime=" + occurTime +
                "} " + super.toString();
    }
}
