package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 同步日志表实体
 */
@Entity
@Table(name = "gat_sync_log", indexes = {
        @Index(name = "idx_log_id", columnList = "logId"),
        @Index(name = "idx_task_id", columnList = "taskId"),
        @Index(name = "idx_controller_id", columnList = "controllerId"),
        @Index(name = "idx_sync_type", columnList = "syncType"),
        @Index(name = "idx_start_time", columnList = "startTime"),
        @Index(name = "idx_result", columnList = "result")
})
public class GatSyncLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_id", length = 32, nullable = false, unique = true)
    private String logId;

    @Column(name = "task_id", length = 32)
    private String taskId;

    @Column(name = "controller_id", length = 12, nullable = false)
    private String controllerId;

    @Column(name = "sync_type", columnDefinition = "TINYINT", nullable = false)
    private Integer syncType; // 1-配置下发，2-状态上报，3-数据采集

    @Column(name = "sync_direction", columnDefinition = "TINYINT", nullable = false)
    private Integer syncDirection; // 1-数据库到设备，2-设备到数据库

    @Column(name = "data_type", length = 32)
    private String dataType;

    @Column(name = "data_size")
    private Integer dataSize; // 数据大小(字节)

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_ms")
    private Integer durationMs; // 耗时(毫秒)

    @Column(name = "result", columnDefinition = "TINYINT")
    private Integer result; // 0-失败，1-成功，2-部分成功

    @Column(name = "success_count", columnDefinition = "INT DEFAULT 0")
    private Integer successCount;

    @Column(name = "failed_count", columnDefinition = "INT DEFAULT 0")
    private Integer failedCount;

    @Column(name = "error_code", length = 16)
    private String errorCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "request_data", columnDefinition = "JSON")
    private String requestData;

    @Column(name = "response_data", columnDefinition = "JSON")
    private String responseData;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 多对一关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "taskId", insertable = false, updatable = false)
    private GatSyncTaskEntity syncTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_id", referencedColumnName = "controllerId", insertable = false, updatable = false)
    private GatSignalControllerEntity controller;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }

    // Constructors
    public GatSyncLogEntity() {}

    public GatSyncLogEntity(String logId, String controllerId, Integer syncType, Integer syncDirection) {
        this.logId = logId;
        this.controllerId = controllerId;
        this.syncType = syncType;
        this.syncDirection = syncDirection;
        this.startTime = LocalDateTime.now();
        this.successCount = 0;
        this.failedCount = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getControllerId() { return controllerId; }
    public void setControllerId(String controllerId) { this.controllerId = controllerId; }

    public Integer getSyncType() { return syncType; }
    public void setSyncType(Integer syncType) { this.syncType = syncType; }

    public Integer getSyncDirection() { return syncDirection; }
    public void setSyncDirection(Integer syncDirection) { this.syncDirection = syncDirection; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public Integer getDataSize() { return dataSize; }
    public void setDataSize(Integer dataSize) { this.dataSize = dataSize; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getDurationMs() { return durationMs; }
    public void setDurationMs(Integer durationMs) { this.durationMs = durationMs; }

    public Integer getResult() { return result; }
    public void setResult(Integer result) { this.result = result; }

    public Integer getSuccessCount() { return successCount; }
    public void setSuccessCount(Integer successCount) { this.successCount = successCount; }

    public Integer getFailedCount() { return failedCount; }
    public void setFailedCount(Integer failedCount) { this.failedCount = failedCount; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getRequestData() { return requestData; }
    public void setRequestData(String requestData) { this.requestData = requestData; }

    public String getResponseData() { return responseData; }
    public void setResponseData(String responseData) { this.responseData = responseData; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public GatSyncTaskEntity getSyncTask() { return syncTask; }
    public void setSyncTask(GatSyncTaskEntity syncTask) { this.syncTask = syncTask; }

    public GatSignalControllerEntity getController() { return controller; }
    public void setController(GatSignalControllerEntity controller) { this.controller = controller; }
}