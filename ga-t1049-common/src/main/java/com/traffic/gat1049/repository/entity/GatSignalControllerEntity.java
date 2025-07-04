package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 信号机控制器表实体
 */
@Entity
@Table(name = "gat_signal_controller", indexes = {
        @Index(name = "idx_controller_id", columnList = "controllerId"),
        @Index(name = "idx_vendor", columnList = "vendor"),
        @Index(name = "idx_device_status", columnList = "deviceStatus"),
        @Index(name = "idx_location", columnList = "longitude,latitude")
})
public class GatSignalControllerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "controller_id", length = 12, nullable = false, unique = true)
    private String controllerId;

    @Column(name = "controller_name", length = 64)
    private String controllerName;

    @Column(name = "vendor", length = 32)
    private String vendor; // 设备厂商：HISENSE/EHUALU/OTHER

    @Column(name = "device_model", length = 64)
    private String deviceModel;

    @Column(name = "device_version", length = 32)
    private String deviceVersion;

    @Column(name = "installation_location", length = 256)
    private String installationLocation;

    @Column(name = "longitude", precision = 10, scale = 7)
    private java.math.BigDecimal longitude;

    @Column(name = "latitude", precision = 10, scale = 7)
    private java.math.BigDecimal latitude;

    @Column(name = "communication_mode", columnDefinition = "TINYINT")
    private Integer communicationMode; // 1-有线，2-无线，3-光纤

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "port", length = 8)
    private String port;

    @Column(name = "communication_params", columnDefinition = "JSON")
    private String communicationParams;

    @Column(name = "device_status", columnDefinition = "TINYINT DEFAULT 0")
    private Integer deviceStatus; // 0-离线，1-在线，2-故障，3-维护

    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    @Column(name = "installation_date")
    private java.time.LocalDate installationDate;

    @Column(name = "maintenance_date")
    private java.time.LocalDate maintenanceDate;

    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status; // 0-禁用，1-启用

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 一对多关系
    @OneToMany(mappedBy = "controller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GatDeviceAdapterInstanceEntity> adapterInstances;

    @OneToMany(mappedBy = "controller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GatSyncTaskEntity> syncTasks;

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
    public GatSignalControllerEntity() {}

    public GatSignalControllerEntity(String controllerId, String controllerName, String vendor) {
        this.controllerId = controllerId;
        this.controllerName = controllerName;
        this.vendor = vendor;
        this.deviceStatus = 0; // 默认离线
        this.status = 1; // 默认启用
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getControllerId() { return controllerId; }
    public void setControllerId(String controllerId) { this.controllerId = controllerId; }

    public String getControllerName() { return controllerName; }
    public void setControllerName(String controllerName) { this.controllerName = controllerName; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getDeviceVersion() { return deviceVersion; }
    public void setDeviceVersion(String deviceVersion) { this.deviceVersion = deviceVersion; }

    public String getInstallationLocation() { return installationLocation; }
    public void setInstallationLocation(String installationLocation) { this.installationLocation = installationLocation; }

    public java.math.BigDecimal getLongitude() { return longitude; }
    public void setLongitude(java.math.BigDecimal longitude) { this.longitude = longitude; }

    public java.math.BigDecimal getLatitude() { return latitude; }
    public void setLatitude(java.math.BigDecimal latitude) { this.latitude = latitude; }

    public Integer getCommunicationMode() { return communicationMode; }
    public void setCommunicationMode(Integer communicationMode) { this.communicationMode = communicationMode; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }

    public String getCommunicationParams() { return communicationParams; }
    public void setCommunicationParams(String communicationParams) { this.communicationParams = communicationParams; }

    public Integer getDeviceStatus() { return deviceStatus; }
    public void setDeviceStatus(Integer deviceStatus) { this.deviceStatus = deviceStatus; }

    public LocalDateTime getLastOnlineTime() { return lastOnlineTime; }
    public void setLastOnlineTime(LocalDateTime lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }

    public java.time.LocalDate getInstallationDate() { return installationDate; }
    public void setInstallationDate(java.time.LocalDate installationDate) { this.installationDate = installationDate; }

    public java.time.LocalDate getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(java.time.LocalDate maintenanceDate) { this.maintenanceDate = maintenanceDate; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<GatDeviceAdapterInstanceEntity> getAdapterInstances() { return adapterInstances; }
    public void setAdapterInstances(List<GatDeviceAdapterInstanceEntity> adapterInstances) { this.adapterInstances = adapterInstances; }

    public List<GatSyncTaskEntity> getSyncTasks() { return syncTasks; }
    public void setSyncTasks(List<GatSyncTaskEntity> syncTasks) { this.syncTasks = syncTasks; }

    @Override
    public String toString() {
        return "GatSignalControllerEntity{" +
                "id=" + id +
                ", controllerId='" + controllerId + '\'' +
                ", controllerName='" + controllerName + '\'' +
                ", vendor='" + vendor + '\'' +
                ", deviceStatus=" + deviceStatus +
                '}';
    }
}