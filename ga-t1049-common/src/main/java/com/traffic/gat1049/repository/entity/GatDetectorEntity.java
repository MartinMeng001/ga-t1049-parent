package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 检测器表实体
 */
@Entity
@Table(name = "gat_detector", indexes = {
        @Index(name = "idx_controller_id", columnList = "controllerId"),
        @Index(name = "idx_detector_type", columnList = "detectorType")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_detector", columnNames = {"controllerId", "detectorId"})
})
public class GatDetectorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "detector_id", length = 32, nullable = false)
    private String detectorId;

    @Column(name = "controller_id", length = 12, nullable = false)
    private String controllerId;

    @Column(name = "detector_name", length = 64)
    private String detectorName;

    @Column(name = "detector_type", columnDefinition = "TINYINT")
    private Integer detectorType; // 1-线圈，2-视频，3-雷达，4-红外

    @Column(name = "installation_position", length = 64)
    private String installationPosition;

    @Column(name = "lane_id", length = 32)
    private String laneId;

    @Column(name = "direction", length = 16)
    private String direction;

    @Column(name = "detection_zone", columnDefinition = "JSON")
    private String detectionZone;

    @Column(name = "sensitivity_level", columnDefinition = "TINYINT")
    private Integer sensitivityLevel; // 1-10

    @Column(name = "detection_params", columnDefinition = "JSON")
    private String detectionParams;

    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status; // 0-故障，1-正常，2-维护

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDetectorId() {
        return detectorId;
    }

    public void setDetectorId(String detectorId) {
        this.detectorId = detectorId;
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }

    public String getDetectorName() {
        return detectorName;
    }

    public void setDetectorName(String detectorName) {
        this.detectorName = detectorName;
    }

    public Integer getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(Integer detectorType) {
        this.detectorType = detectorType;
    }

    public String getInstallationPosition() {
        return installationPosition;
    }

    public void setInstallationPosition(String installationPosition) {
        this.installationPosition = installationPosition;
    }

    public String getLaneId() {
        return laneId;
    }

    public void setLaneId(String laneId) {
        this.laneId = laneId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDetectionZone() {
        return detectionZone;
    }

    public void setDetectionZone(String detectionZone) {
        this.detectionZone = detectionZone;
    }

    public Integer getSensitivityLevel() {
        return sensitivityLevel;
    }

    public void setSensitivityLevel(Integer sensitivityLevel) {
        this.sensitivityLevel = sensitivityLevel;
    }

    public String getDetectionParams() {
        return detectionParams;
    }

    public void setDetectionParams(String detectionParams) {
        this.detectionParams = detectionParams;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
