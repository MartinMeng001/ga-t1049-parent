package com.traffic.gat1049.model.entity.runtime;

/**
 * 干线控制方式
 * 对应文档中的 RouteControlMode
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "RouteControlMode")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteControlModeStatus extends BaseState {

    /**
     * 线路编号
     */
    @NotBlank(message = "线路编号不能为空")
    @XmlElement(name = "RouteID", required = true)
    @JsonProperty("RouteID")
    private String routeId;

    /**
     * 控制方式
     */
    @XmlElement(name = "Value", required = true)
    @JsonProperty("Value")
    private RouteControlMode value;

    // 构造函数
    public RouteControlModeStatus() {
        super();
    }

    public RouteControlModeStatus(String routeId, RouteControlMode value) {
        super();
        this.routeId = routeId;
        this.value = value;
    }

    // Getters and Setters
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public RouteControlMode getValue() {
        return value;
    }

    public void setValue(RouteControlMode value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RouteControlModeStatus{" +
                "routeId='" + routeId + '\'' +
                ", value=" + value +
                "} " + super.toString();
    }
}
