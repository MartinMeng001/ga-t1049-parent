package com.traffic.gat1049.model.entity.system;

/**
 * 系统状态
 * 对应文档中的 SysState
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "SysState")
@XmlAccessorType(XmlAccessType.FIELD)
public class SysState extends BaseState {

    /**
     * 系统运行状态
     */
    @XmlElement(name = "Value", required = true)
    @JsonProperty("Value")
    private SystemState value;

    // 构造函数
    public SysState() {
        super();
    }

    public SysState(SystemState value) {
        super();
        this.value = value;
    }

    // Getters and Setters
    public SystemState getValue() {
        return value;
    }

    public void setValue(SystemState value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SysState{" +
                "value=" + value +
                "} " + super.toString();
    }
}
