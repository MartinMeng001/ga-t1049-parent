package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 系统状态表实体
 */
@Entity
@Table(name = "gat_sys_state", indexes = {
        @Index(name = "idx_system_id", columnList = "systemId"),
        @Index(name = "idx_current_time", columnList = "currentTime"),
        @Index(name = "idx_running_state", columnList = "runningState")
})
public class GatSysStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_id", length = 64, nullable = false)
    private String systemId;

    @Column(name = "current_time", nullable = false)
    private LocalDateTime currentTime;

    @Column(name = "running_state", columnDefinition = "TINYINT")
    private Integer runningState; // 0-停止，1-运行，2-故障

    @Column(name = "work_mode", columnDefinition = "TINYINT")
    private Integer workMode; // 0-手动，1-自动，2-维护

    @Column(name = "cpu_usage", precision = 5, scale = 2)
    private BigDecimal cpuUsage;

    @Column(name = "memory_usage", precision = 5, scale = 2)
    private BigDecimal memoryUsage;

    @Column(name = "disk_usage", precision = 5, scale = 2)
    private BigDecimal diskUsage;

    @Column(name = "network_status", columnDefinition = "TINYINT")
    private Integer networkStatus; // 0-断开，1-连接

    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;

    @Column(name = "error_count", columnDefinition = "INT DEFAULT 0")
    private Integer errorCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 外键关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", referencedColumnName = "systemId", insertable = false, updatable = false)
    private GatSysInfoEntity sysInfo;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public GatSysStateEntity() {}

    public GatSysStateEntity(String systemId, LocalDateTime currentTime) {
        this.systemId = systemId;
        this.currentTime = currentTime;
        this.errorCount = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSystemId() { return systemId; }
    public void setSystemId(String systemId) { this.systemId = systemId; }

    public LocalDateTime getCurrentTime() { return currentTime; }
    public void setCurrentTime(LocalDateTime currentTime) { this.currentTime = currentTime; }

    public Integer getRunningState() { return runningState; }
    public void setRunningState(Integer runningState) { this.runningState = runningState; }

    public Integer getWorkMode() { return workMode; }
    public void setWorkMode(Integer workMode) { this.workMode = workMode; }

    public BigDecimal getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(BigDecimal cpuUsage) { this.cpuUsage = cpuUsage; }

    public BigDecimal getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(BigDecimal memoryUsage) { this.memoryUsage = memoryUsage; }

    public BigDecimal getDiskUsage() { return diskUsage; }
    public void setDiskUsage(BigDecimal diskUsage) { this.diskUsage = diskUsage; }

    public Integer getNetworkStatus() { return networkStatus; }
    public void setNetworkStatus(Integer networkStatus) { this.networkStatus = networkStatus; }

    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public Integer getErrorCount() { return errorCount; }
    public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public GatSysInfoEntity getSysInfo() { return sysInfo; }
    public void setSysInfo(GatSysInfoEntity sysInfo) { this.sysInfo = sysInfo; }
}