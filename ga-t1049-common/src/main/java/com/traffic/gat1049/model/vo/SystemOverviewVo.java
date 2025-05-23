package com.traffic.gat1049.model.vo;

/**
 * 系统概览视图对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SystemOverviewVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统名称
     */
    @JsonProperty("systemName")
    private String systemName;

    /**
     * 系统版本
     */
    @JsonProperty("systemVersion")
    private String systemVersion;

    /**
     * 系统状态
     */
    @JsonProperty("systemState")
    private SystemState systemState;

    /**
     * 路口总数
     */
    @JsonProperty("totalCrossCount")
    private Integer totalCrossCount;

    /**
     * 在线路口数
     */
    @JsonProperty("onlineCrossCount")
    private Integer onlineCrossCount;

    /**
     * 离线路口数
     */
    @JsonProperty("offlineCrossCount")
    private Integer offlineCrossCount;

    /**
     * 故障路口数
     */
    @JsonProperty("errorCrossCount")
    private Integer errorCrossCount;

    /**
     * 信号机总数
     */
    @JsonProperty("totalSignalControllerCount")
    private Integer totalSignalControllerCount;

    /**
     * 在线信号机数
     */
    @JsonProperty("onlineSignalControllerCount")
    private Integer onlineSignalControllerCount;

    /**
     * 系统运行时间（秒）
     */
    @JsonProperty("systemUptime")
    private Long systemUptime;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("lastUpdateTime")
    private LocalDateTime lastUpdateTime;

    // Getters and Setters
    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public SystemState getSystemState() {
        return systemState;
    }

    public void setSystemState(SystemState systemState) {
        this.systemState = systemState;
    }

    public Integer getTotalCrossCount() {
        return totalCrossCount;
    }

    public void setTotalCrossCount(Integer totalCrossCount) {
        this.totalCrossCount = totalCrossCount;
    }

    public Integer getOnlineCrossCount() {
        return onlineCrossCount;
    }

    public void setOnlineCrossCount(Integer onlineCrossCount) {
        this.onlineCrossCount = onlineCrossCount;
    }

    public Integer getOfflineCrossCount() {
        return offlineCrossCount;
    }

    public void setOfflineCrossCount(Integer offlineCrossCount) {
        this.offlineCrossCount = offlineCrossCount;
    }

    public Integer getErrorCrossCount() {
        return errorCrossCount;
    }

    public void setErrorCrossCount(Integer errorCrossCount) {
        this.errorCrossCount = errorCrossCount;
    }

    public Integer getTotalSignalControllerCount() {
        return totalSignalControllerCount;
    }

    public void setTotalSignalControllerCount(Integer totalSignalControllerCount) {
        this.totalSignalControllerCount = totalSignalControllerCount;
    }

    public Integer getOnlineSignalControllerCount() {
        return onlineSignalControllerCount;
    }

    public void setOnlineSignalControllerCount(Integer onlineSignalControllerCount) {
        this.onlineSignalControllerCount = onlineSignalControllerCount;
    }

    public Long getSystemUptime() {
        return systemUptime;
    }

    public void setSystemUptime(Long systemUptime) {
        this.systemUptime = systemUptime;
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "SystemOverviewVo{" +
                "systemName='" + systemName + '\'' +
                ", systemVersion='" + systemVersion + '\'' +
                ", systemState=" + systemState +
                ", totalCrossCount=" + totalCrossCount +
                ", onlineCrossCount=" + onlineCrossCount +
                ", offlineCrossCount=" + offlineCrossCount +
                ", errorCrossCount=" + errorCrossCount +
                ", totalSignalControllerCount=" + totalSignalControllerCount +
                ", onlineSignalControllerCount=" + onlineSignalControllerCount +
                ", systemUptime=" + systemUptime +
                ", lastUpdateTime=" + lastUpdateTime +
                '}';
    }
}
