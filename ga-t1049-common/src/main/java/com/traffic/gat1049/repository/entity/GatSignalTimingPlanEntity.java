package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 信号配时方案表实体
 */
@Entity
@Table(name = "gat_signal_timing_plan", indexes = {
        @Index(name = "idx_plan_id", columnList = "planId"),
        @Index(name = "idx_controller_id", columnList = "controllerId"),
        @Index(name = "idx_plan_type", columnList = "planType"),
        @Index(name = "idx_status", columnList = "status")
})
public class GatSignalTimingPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", length = 32, nullable = false, unique = true)
    private String planId;

    @Column(name = "controller_id", length = 12, nullable = false)
    private String controllerId;

    @Column(name = "plan_name", length = 64, nullable = false)
    private String planName;

    @Column(name = "plan_type", columnDefinition = "TINYINT", nullable = false)
    private Integer planType; // 1-固定配时，2-感应配时，3-优化配时

    @Column(name = "cycle_time", columnDefinition = "INT DEFAULT 60")
    private Integer cycleTime; // 周期时长(秒)

    @Column(name = "phase_config", columnDefinition = "JSON", nullable = false)
    private String phaseConfig; // 相位配置

    @Column(name = "timing_config", columnDefinition = "JSON", nullable = false)
    private String timingConfig; // 配时配置

    @Column(name = "effective_time", columnDefinition = "JSON")
    private String effectiveTime; // 生效时间配置

    @Column(name = "priority", columnDefinition = "TINYINT DEFAULT 5")
    private Integer priority; // 优先级(1-10)

    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status; // 0-禁用，1-启用

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 外键关系
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
    public GatSignalTimingPlanEntity() {}

    public GatSignalTimingPlanEntity(String planId, String controllerId, String planName, Integer planType, String phaseConfig, String timingConfig) {
        this.planId = planId;
        this.controllerId = controllerId;
        this.planName = planName;
        this.planType = planType;
        this.phaseConfig = phaseConfig;
        this.timingConfig = timingConfig;
        this.cycleTime = 60;
        this.priority = 5;
        this.status = 1;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }

    public String getControllerId() { return controllerId; }
    public void setControllerId(String controllerId) { this.controllerId = controllerId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public Integer getPlanType() { return planType; }
    public void setPlanType(Integer planType) { this.planType = planType; }

    public Integer getCycleTime() { return cycleTime; }
    public void setCycleTime(Integer cycleTime) { this.cycleTime = cycleTime; }

    public String getPhaseConfig() { return phaseConfig; }
    public void setPhaseConfig(String phaseConfig) { this.phaseConfig = phaseConfig; }

    public String getTimingConfig() { return timingConfig; }
    public void setTimingConfig(String timingConfig) { this.timingConfig = timingConfig; }

    public String getEffectiveTime() { return effectiveTime; }
    public void setEffectiveTime(String effectiveTime) { this.effectiveTime = effectiveTime; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public GatSignalControllerEntity getSignalController() { return signalController; }
    public void setSignalController(GatSignalControllerEntity signalController) { this.signalController = signalController; }

    @Override
    public String toString() {
        return "GatSignalTimingPlanEntity{" +
                "id=" + id +
                ", planId='" + planId + '\'' +
                ", controllerId='" + controllerId + '\'' +
                ", planName='" + planName + '\'' +
                ", planType=" + planType +
                ", cycleTime=" + cycleTime +
                ", status=" + status +
                '}';
    }
}
