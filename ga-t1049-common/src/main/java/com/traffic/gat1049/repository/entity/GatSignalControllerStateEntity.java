package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 信号机控制器状态表实体
 */
@Entity
@Table(name = "gat_signal_controller_state", indexes = {
        @Index(name = "idx_controller_id", columnList = "controllerId"),
        @Index(name = "idx_operation_state", columnList = "operationState"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_controller_status_time", columnList = "controllerId,operationState,createdAt")
})
public class GatSignalControllerStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "controller_id", length = 12, nullable = false)
    private String controllerId;

    @Column(name = "operation_state", columnDefinition = "TINYINT")
    private Integer operationState; // 0-停机，1-运行，2-故障，3-维护

    @Column(name = "control_mode", columnDefinition = "TINYINT")
    private Integer controlMode; // 0-手动，1-自动，2-感应，3-优化

    @Column(name = "current_plan_id", length = 32)
    private String currentPlanId;

    @Column(name = "phase_status", columnDefinition = "JSON")
    private String phaseStatus; // 相位状态信息

    @Column(name = "detector_status", columnDefinition = "JSON")
    private String detectorStatus; // 检测器状态信息

    @Column(name = "fault_code", length = 16)
    private String faultCode;

    @Column(name = "fault_description", columnDefinition = "TEXT")
    private String faultDescription;

    @Column(name = "last_heartbeat", nullable = false)
    private LocalDateTime lastHeartbeat;

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
    public GatSignalControllerStateEntity() {}

    public GatSignalControllerStateEntity(String controllerId, Integer operationState, LocalDateTime lastHeartbeat) {
        this.controllerId = controllerId;
        this.operationState = operationState;
        this.lastHeartbeat = lastHeartbeat;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getControllerId() { return controllerId; }
    public void setControllerId(String controllerId) { this.controllerId = controllerId; }

    public Integer getOperationState() { return operationState; }
    public void setOperationState(Integer operationState) { this.operationState = operationState; }

    public Integer getControlMode() { return controlMode; }
    public void setControlMode(Integer controlMode) { this.controlMode = controlMode; }

    public String getCurrentPlanId() { return currentPlanId; }
    public void setCurrentPlanId(String currentPlanId) { this.currentPlanId = currentPlanId; }

    public String getPhaseStatus() { return phaseStatus; }
    public void setPhaseStatus(String phaseStatus) { this.phaseStatus = phaseStatus; }

    public String getDetectorStatus() { return detectorStatus; }
    public void setDetectorStatus(String detectorStatus) { this.detectorStatus = detectorStatus; }

    public String getFaultCode() { return faultCode; }
    public void setFaultCode(String faultCode) { this.faultCode = faultCode; }

    public String getFaultDescription() { return faultDescription; }
    public void setFaultDescription(String faultDescription) { this.faultDescription = faultDescription; }

    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public GatSignalControllerEntity getSignalController() { return signalController; }
    public void setSignalController(GatSignalControllerEntity signalController) { this.signalController = signalController; }

    @Override
    public String toString() {
        return "GatSignalControllerStateEntity{" +
                "id=" + id +
                ", controllerId='" + controllerId + '\'' +
                ", operationState=" + operationState +
                ", controlMode=" + controlMode +
                ", currentPlanId='" + currentPlanId + '\'' +
                ", lastHeartbeat=" + lastHeartbeat +
                '}';
    }
}
