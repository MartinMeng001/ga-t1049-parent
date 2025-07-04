package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 告警规则表实体
 */
@Entity
@Table(name = "gat_alarm_rule", indexes = {
        @Index(name = "idx_rule_id", columnList = "ruleId"),
        @Index(name = "idx_rule_type", columnList = "ruleType"),
        @Index(name = "idx_enabled", columnList = "enabled")
})
public class GatAlarmRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id", length = 32, nullable = false, unique = true)
    private String ruleId;

    @Column(name = "rule_name", length = 64, nullable = false)
    private String ruleName;

    @Column(name = "rule_type", columnDefinition = "TINYINT", nullable = false)
    private Integer ruleType; // 1-设备状态，2-通信状态，3-性能指标

    @Column(name = "condition_expression", columnDefinition = "JSON", nullable = false)
    private String conditionExpression;

    @Column(name = "alarm_level", columnDefinition = "TINYINT", nullable = false)
    private Integer alarmLevel; // 1-信息，2-警告，3-错误，4-严重

    @Column(name = "notification_config", columnDefinition = "JSON")
    private String notificationConfig;

    @Column(name = "enabled", columnDefinition = "TINYINT DEFAULT 1")
    private Integer enabled; // 0-禁用，1-启用

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
    public GatAlarmRuleEntity() {}

    public GatAlarmRuleEntity(String ruleId, String ruleName, Integer ruleType, String conditionExpression, Integer alarmLevel) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.ruleType = ruleType;
        this.conditionExpression = conditionExpression;
        this.alarmLevel = alarmLevel;
        this.enabled = 1;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public Integer getRuleType() { return ruleType; }
    public void setRuleType(Integer ruleType) { this.ruleType = ruleType; }

    public String getConditionExpression() { return conditionExpression; }
    public void setConditionExpression(String conditionExpression) { this.conditionExpression = conditionExpression; }

    public Integer getAlarmLevel() { return alarmLevel; }
    public void setAlarmLevel(Integer alarmLevel) { this.alarmLevel = alarmLevel; }

    public String getNotificationConfig() { return notificationConfig; }
    public void setNotificationConfig(String notificationConfig) { this.notificationConfig = notificationConfig; }

    public Integer getEnabled() { return enabled; }
    public void setEnabled(Integer enabled) { this.enabled = enabled; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "GatAlarmRuleEntity{" +
                "id=" + id +
                ", ruleId='" + ruleId + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", ruleType=" + ruleType +
                ", alarmLevel=" + alarmLevel +
                ", enabled=" + enabled +
                '}';
    }
}
