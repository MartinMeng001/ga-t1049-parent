package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 同步任务表实体
 */
@Entity
@Table(name = "gat_sync_task", indexes = {
        @Index(name = "idx_task_id", columnList = "taskId"),
        @Index(name = "idx_controller_id", columnList = "controllerId"),
        @Index(name = "idx_task_type", columnList = "taskType"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_next_execute_time", columnList = "nextExecuteTime")
})
public class GatSyncTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", length = 32, nullable = false, unique = true)
    private String taskId;

    @Column(name = "task_name", length = 64, nullable = false)
    private String taskName;

    @Column(name = "task_type", columnDefinition = "TINYINT", nullable = false)
    private Integer taskType; // 1-配置下发，2-状态上报，3-数据采集

    @Column(name = "controller_id", length = 12, nullable = false)
    private String controllerId;

    @Column(name = "sync_direction", columnDefinition = "TINYINT", nullable = false)
    private Integer syncDirection; // 1-数据库到设备，2-设备到数据库

    @Column(name = "data_type", length = 32)
    private String dataType;

    @Column(name = "sync_content", columnDefinition = "JSON")
    private String syncContent;

    @Column(name = "schedule_type", columnDefinition = "TINYINT")
    private Integer scheduleType; // 1-立即执行，2-定时执行，3-周期执行

    @Column(name = "schedule_config", columnDefinition = "JSON")
    private String scheduleConfig;

    @Column(name = "priority_level", columnDefinition = "TINYINT DEFAULT 5")
    private Integer priorityLevel; // 优先级(1-10)

    @Column(name = "max_retry_count", columnDefinition = "INT DEFAULT 3")
    private Integer maxRetryCount;

    @Column(name = "timeout_seconds", columnDefinition = "INT DEFAULT 30")
    private Integer timeoutSeconds;

    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 0")
    private Integer status; // 0-待执行，1-执行中，2-成功，3-失败，4-取消

    @Column(name = "last_execute_time")
    private LocalDateTime lastExecuteTime;

    @Column(name = "next_execute_time")
    private LocalDateTime nextExecuteTime;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 多对一关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_id", referencedColumnName = "controllerId", insertable = false, updatable = false)
    private GatSignalControllerEntity controller;

    // 一对多关系
    @OneToMany(mappedBy = "syncTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GatSyncLogEntity> syncLogs;

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
    public GatSyncTaskEntity() {}

    public GatSyncTaskEntity(String taskId, String taskName, Integer taskType, String controllerId, Integer syncDirection) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskType = taskType;
        this.controllerId = controllerId;
        this.syncDirection = syncDirection;
        this.priorityLevel = 5;
        this.maxRetryCount = 3;
        this.timeoutSeconds = 30;
        this.status = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public Integer getTaskType() { return taskType; }
    public void setTaskType(Integer taskType) { this.taskType = taskType; }

    public String getControllerId() { return controllerId; }
    public void setControllerId(String controllerId) { this.controllerId = controllerId; }

    public Integer getSyncDirection() { return syncDirection; }
    public void setSyncDirection(Integer syncDirection) { this.syncDirection = syncDirection; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public String getSyncContent() { return syncContent; }
    public void setSyncContent(String syncContent) { this.syncContent = syncContent; }

    public Integer getScheduleType() { return scheduleType; }
    public void setScheduleType(Integer scheduleType) { this.scheduleType = scheduleType; }

    public String getScheduleConfig() { return scheduleConfig; }
    public void setScheduleConfig(String scheduleConfig) { this.scheduleConfig = scheduleConfig; }

    public Integer getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(Integer priorityLevel) { this.priorityLevel = priorityLevel; }

    public Integer getMaxRetryCount() { return maxRetryCount; }
    public void setMaxRetryCount(Integer maxRetryCount) { this.maxRetryCount = maxRetryCount; }

    public Integer getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getLastExecuteTime() { return lastExecuteTime; }
    public void setLastExecuteTime(LocalDateTime lastExecuteTime) { this.lastExecuteTime = lastExecuteTime; }

    public LocalDateTime getNextExecuteTime() { return nextExecuteTime; }
    public void setNextExecuteTime(LocalDateTime nextExecuteTime) { this.nextExecuteTime = nextExecuteTime; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public GatSignalControllerEntity getController() { return controller; }
    public void setController(GatSignalControllerEntity controller) { this.controller = controller; }

    public List<GatSyncLogEntity> getSyncLogs() { return syncLogs; }
    public void setSyncLogs(List<GatSyncLogEntity> syncLogs) { this.syncLogs = syncLogs; }
}