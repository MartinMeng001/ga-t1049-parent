package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 操作日志表实体
 */
@Entity
@Table(name = "gat_operation_log", indexes = {
        @Index(name = "idx_log_id", columnList = "logId"),
        @Index(name = "idx_user_id", columnList = "userId"),
        @Index(name = "idx_operation_type", columnList = "operationType"),
        @Index(name = "idx_operation_time", columnList = "operationTime"),
        @Index(name = "idx_operation_result", columnList = "operationResult")
})
public class GatOperationLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_id", length = 32, nullable = false, unique = true)
    private String logId;

    @Column(name = "user_id", length = 32)
    private String userId;

    @Column(name = "username", length = 64)
    private String username;

    @Column(name = "operation_type", length = 32, nullable = false)
    private String operationType;

    @Column(name = "operation_name", length = 64, nullable = false)
    private String operationName;

    @Column(name = "operation_description", columnDefinition = "TEXT")
    private String operationDescription;

    @Column(name = "target_type", length = 32)
    private String targetType;

    @Column(name = "target_id", length = 64)
    private String targetId;

    @Column(name = "request_params", columnDefinition = "JSON")
    private String requestParams;

    @Column(name = "response_data", columnDefinition = "JSON")
    private String responseData;

    @Column(name = "operation_result", columnDefinition = "TINYINT")
    private Integer operationResult; // 0-失败，1-成功

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "client_ip", length = 45)
    private String clientIp;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "operation_time", nullable = false)
    private LocalDateTime operationTime;

    @Column(name = "duration_ms")
    private Integer durationMs; // 操作耗时(毫秒)

    // 外键关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", insertable = false, updatable = false)
    private GatUserEntity user;

    @PrePersist
    protected void onCreate() {
        if (operationTime == null) {
            operationTime = LocalDateTime.now();
        }
    }

    // Constructors
    public GatOperationLogEntity() {}

    public GatOperationLogEntity(String logId, String operationType, String operationName) {
        this.logId = logId;
        this.operationType = operationType;
        this.operationName = operationName;
        this.operationTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getOperationName() { return operationName; }
    public void setOperationName(String operationName) { this.operationName = operationName; }

    public String getOperationDescription() { return operationDescription; }
    public void setOperationDescription(String operationDescription) { this.operationDescription = operationDescription; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public String getRequestParams() { return requestParams; }
    public void setRequestParams(String requestParams) { this.requestParams = requestParams; }

    public String getResponseData() { return responseData; }
    public void setResponseData(String responseData) { this.responseData = responseData; }

    public Integer getOperationResult() { return operationResult; }
    public void setOperationResult(Integer operationResult) { this.operationResult = operationResult; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getOperationTime() { return operationTime; }
    public void setOperationTime(LocalDateTime operationTime) { this.operationTime = operationTime; }

    public Integer getDurationMs() { return durationMs; }
    public void setDurationMs(Integer durationMs) { this.durationMs = durationMs; }

    public GatUserEntity getUser() { return user; }
    public void setUser(GatUserEntity user) { this.user = user; }

    @Override
    public String toString() {
        return "GatOperationLogEntity{" +
                "id=" + id +
                ", logId='" + logId + '\'' +
                ", userId='" + userId + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationName='" + operationName + '\'' +
                ", operationResult=" + operationResult +
                ", operationTime=" + operationTime +
                '}';
    }
}
