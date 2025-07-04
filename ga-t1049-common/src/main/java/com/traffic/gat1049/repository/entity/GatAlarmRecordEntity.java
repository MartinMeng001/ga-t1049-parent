package com.traffic.gat1049.repository.entity;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 告警记录表实体
 */
@Entity
@Table(name = "gat_alarm_record", indexes = {
        @Index(name = "idx_alarm_id", columnList = "alarmId"),
        @Index(name = "idx_rule_id", columnList = "ruleId"),
        @Index(name = "idx_controller_id", columnList = "controllerId"),
        @Index(name = "idx_alarm_type", columnList = "alarmType"),
        @Index(name = "idx_alarm_level", columnList = "alarmLevel"),
        @Index(name = "idx_alarm_time", columnList = "alarmTime"),
        @Index(name = "idx_status", columnList = "status")
})
public class GatAlarmRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alarm_id", length = 32, nullable = false, unique = true)
    private String alarmId;

    @Column(name = "rule_id", length = 32)
    private String ruleId;

    @Column(name = "controller_id", length = 12)
    private String controllerId;

    @Column(name = "alarm_type", columnDefinition = "TINYINT", nullable = false)
    private Integer alarmType; // 1-设备故障，2-通信异常，3-性能告警

    @Column(name = "alarm_level", columnDefinition = "TINYINT", nullable = false)
    private Integer alarmLevel; // 1-信息，2-警告，3-错误，4-严重

    @Column(name = "alarm_title", length = 128, nullable = false)
    private String alarmTitle;

    @Column(name = "alarm_description", columnDefinition = "TEXT")
    private String alarmDescription;

    @Column(name = "alarm_time", nullable = false)
    private LocalDateTime alarmTime;

    @Column(name = "trigger_data", columnDefinition = "JSON")
    private String triggerData;

    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 0")
    private Integer status; // 0-未处理，1-处理中，2-已处理，3-已忽略

    @Column(name = "acknowledged_by", length = 64)
    private String acknowledgedBy;

    @Column(name = "acknowledged_time")
    private LocalDateTime acknowledgedTime;

    @Column(name = "resolved_by", length = 64)
    private String resolvedBy;

    @Column(name = "resolved_time")
    private LocalDateTime resolvedTime;

    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;

    @Column(name = "notification_sent", columnDefinition = "TINYINT DEFAULT 0")
    private Integer notificationSent; // 0-否，1-是

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 外键关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", referencedColumnName = "ruleId", insertable = false, updatable = false)
    private GatAlarmRuleEntity alarmRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_id", referencedColumnName = "controllerId", insertable = false, updatable = false)
    private GatSignalControllerEntity signalController;

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
    public GatAlarmRecordEntity() {}

    public GatAlarmRecordEntity(String alarmId, Integer alarmType, Integer alarmLevel, String alarmTitle, LocalDateTime alarmTime) {
        this.alarmId = alarmId;
        this.alarmType = alarmType;
        this.alarmLevel = alarmLevel;
        this.alarmTitle = alarmTitle;
        this.alarmTime = alarmTime;
        this.status = 0;
        this.notificationSent = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAlarmId() { return alarmId; }
    public void setAlarmId(String alarmId) { this.alarmId = alarmId; }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public String getControllerId() { return controllerId; }
    public void setControllerId(String controllerId) { this.controllerId = controllerId; }

    public Integer getAlarmType() { return alarmType; }
    public void setAlarmType(Integer alarmType) { this.alarmType = alarmType; }

    public Integer getAlarmLevel() { return alarmLevel; }
    public void setAlarmLevel(Integer alarmLevel) { this.alarmLevel = alarmLevel; }

    public String getAlarmTitle() { return alarmTitle; }
    public void setAlarmTitle(String alarmTitle) { this.alarmTitle = alarmTitle; }

    public String getAlarmDescription() { return alarmDescription; }
    public void setAlarmDescription(String alarmDescription) { this.alarmDescription = alarmDescription; }

    public LocalDateTime getAlarmTime() { return alarmTime; }
    public void setAlarmTime(LocalDateTime alarmTime) { this.alarmTime = alarmTime; }

    public String getTriggerData() { return triggerData; }
    public void setTriggerData(String triggerData) { this.triggerData = triggerData; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }

    public LocalDateTime getAcknowledgedTime() { return acknowledgedTime; }
    public void setAcknowledgedTime(LocalDateTime acknowledgedTime) { this.acknowledgedTime = acknowledgedTime; }

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }

    public LocalDateTime getResolvedTime() { return resolvedTime; }
    public void setResolvedTime(LocalDateTime resolvedTime) { this.resolvedTime = resolvedTime; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public Integer getNotificationSent() { return notificationSent; }
    public void setNotificationSent(Integer notificationSent) { this.notificationSent = notificationSent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public GatAlarmRuleEntity getAlarmRule() { return alarmRule; }
    public void setAlarmRule(GatAlarmRuleEntity alarmRule) { this.alarmRule = alarmRule; }

    public GatSignalControllerEntity getSignalController() { return signalController; }
    public void setSignalController(GatSignalControllerEntity signalController) { this.signalController = signalController; }

    @Override
    public String toString() {
        return "GatAlarmRecordEntity{" +
                "id=" + id +
                ", alarmId='" + alarmId + '\'' +
                ", alarmType=" + alarmType +
                ", alarmLevel=" + alarmLevel +
                ", alarmTitle='" + alarmTitle + '\'' +
                ", alarmTime=" + alarmTime +
                ", status=" + status +
                '}';
    }
}
