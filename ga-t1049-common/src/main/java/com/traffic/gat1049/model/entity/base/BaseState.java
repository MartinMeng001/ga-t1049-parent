package com.traffic.gat1049.model.entity.base;

/**
 * 运行状态基类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BaseState extends BaseEntity {

    /**
     * 状态更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @XmlElement(name = "StateTime")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime stateTime;

    /**
     * 状态有效期（秒）
     */
    @XmlElement(name = "ValidityPeriod")
    private Integer validityPeriod;

    public BaseState() {
        super();
        this.stateTime = LocalDateTime.now();
    }

    public LocalDateTime getStateTime() {
        return stateTime;
    }

    public void setStateTime(LocalDateTime stateTime) {
        this.stateTime = stateTime;
    }

    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    /**
     * 检查状态是否有效
     */
    public boolean isValid() {
        if (validityPeriod == null || stateTime == null) {
            return true;
        }
        return LocalDateTime.now().isBefore(stateTime.plusSeconds(validityPeriod));
    }

    @Override
    public String toString() {
        return "BaseState{" +
                "stateTime=" + stateTime +
                ", validityPeriod=" + validityPeriod +
                "} " + super.toString();
    }
}
