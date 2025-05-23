package com.traffic.gat1049.model.dto;

/**
 * 路口查询DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CrossQueryDto extends PageRequestDto {

    /**
     * 路口编号
     */
    @JsonProperty("crossId")
    private String crossId;

    /**
     * 路口名称
     */
    @JsonProperty("crossName")
    private String crossName;

    /**
     * 区域编号
     */
    @JsonProperty("regionId")
    private String regionId;

    /**
     * 路口状态
     */
    @JsonProperty("state")
    private SystemState state;

    /**
     * 信号机编号
     */
    @JsonProperty("signalControllerId")
    private String signalControllerId;

    // Getters and Setters
    public String getCrossId() {
        return crossId;
    }

    public void setCrossId(String crossId) {
        this.crossId = crossId;
    }

    public String getCrossName() {
        return crossName;
    }

    public void setCrossName(String crossName) {
        this.crossName = crossName;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public SystemState getState() {
        return state;
    }

    public void setState(SystemState state) {
        this.state = state;
    }

    public String getSignalControllerId() {
        return signalControllerId;
    }

    public void setSignalControllerId(String signalControllerId) {
        this.signalControllerId = signalControllerId;
    }

    @Override
    public String toString() {
        return "CrossQueryDto{" +
                "crossId='" + crossId + '\'' +
                ", crossName='" + crossName + '\'' +
                ", regionId='" + regionId + '\'' +
                ", state=" + state +
                ", signalControllerId='" + signalControllerId + '\'' +
                "} " + super.toString();
    }
}
