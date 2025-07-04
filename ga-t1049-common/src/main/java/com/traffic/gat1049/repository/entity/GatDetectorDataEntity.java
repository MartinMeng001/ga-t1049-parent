package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 检测器数据表实体
 */
@Entity
@Table(name = "gat_detector_data", indexes = {
        @Index(name = "idx_controller_id", columnList = "controllerId"),
        @Index(name = "idx_detector_id", columnList = "detectorId"),
        @Index(name = "idx_detection_time", columnList = "detectionTime"),
        @Index(name = "idx_detector_time_type", columnList = "detectionTime,controllerId,detectorId")
})
public class GatDetectorDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "controller_id", length = 12, nullable = false)
    private String controllerId;

    @Column(name = "detector_id", length = 16, nullable = false)
    private String detectorId;

    @Column(name = "detection_time", nullable = false)
    private LocalDateTime detectionTime;

    @Column(name = "vehicle_count", columnDefinition = "INT DEFAULT 0")
    private Integer vehicleCount; // 车辆数

    @Column(name = "occupancy_rate", precision = 5, scale = 2)
    private BigDecimal occupancyRate; // 占有率(%)

    @Column(name = "average_speed", precision = 5, scale = 2)
    private BigDecimal averageSpeed; // 平均速度(km/h)

    @Column(name = "flow_rate", precision = 8, scale = 2)
    private BigDecimal flowRate; // 流量(辆/小时)

    @Column(name = "density", precision = 8, scale = 2)
    private BigDecimal density; // 密度(辆/km)

    @Column(name = "detector_status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer detectorStatus; // 0-故障，1-正常

    @Column(name = "raw_data", columnDefinition = "JSON")
    private String rawData; // 原始数据

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 外键关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_id", referencedColumnName = "controllerId", insertable = false, updatable = false)
    private GatSignalControllerEntity signalController;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public GatDetectorDataEntity() {}

    public GatDetectorDataEntity(String controllerId, String detectorId, LocalDateTime detectionTime) {
        this.controllerId = controllerId;
        this.detectorId = detectorId;
        this.detectionTime = detectionTime;
        this.vehicleCount = 0;
        this.detectorStatus = 1;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getControllerId() { return controllerId; }
    public void setControllerId(String controllerId) { this.controllerId = controllerId; }

    public String getDetectorId() { return detectorId; }
    public void setDetectorId(String detectorId) { this.detectorId = detectorId; }

    public LocalDateTime getDetectionTime() { return detectionTime; }
    public void setDetectionTime(LocalDateTime detectionTime) { this.detectionTime = detectionTime; }

    public Integer getVehicleCount() { return vehicleCount; }
    public void setVehicleCount(Integer vehicleCount) { this.vehicleCount = vehicleCount; }

    public BigDecimal getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(BigDecimal occupancyRate) { this.occupancyRate = occupancyRate; }

    public BigDecimal getAverageSpeed() { return averageSpeed; }
    public void setAverageSpeed(BigDecimal averageSpeed) { this.averageSpeed = averageSpeed; }

    public BigDecimal getFlowRate() { return flowRate; }
    public void setFlowRate(BigDecimal flowRate) { this.flowRate = flowRate; }

    public BigDecimal getDensity() { return density; }
    public void setDensity(BigDecimal density) { this.density = density; }

    public Integer getDetectorStatus() { return detectorStatus; }
    public void setDetectorStatus(Integer detectorStatus) { this.detectorStatus = detectorStatus; }

    public String getRawData() { return rawData; }
    public void setRawData(String rawData) { this.rawData = rawData; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public GatSignalControllerEntity getSignalController() { return signalController; }
    public void setSignalController(GatSignalControllerEntity signalController) { this.signalController = signalController; }

    @Override
    public String toString() {
        return "GatDetectorDataEntity{" +
                "id=" + id +
                ", controllerId='" + controllerId + '\'' +
                ", detectorId='" + detectorId + '\'' +
                ", detectionTime=" + detectionTime +
                ", vehicleCount=" + vehicleCount +
                ", detectorStatus=" + detectorStatus +
                '}';
    }
}
