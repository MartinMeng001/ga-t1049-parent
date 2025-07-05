package com.traffic.gat1049.repository.entity;

import com.traffic.gat1049.model.enums.CommMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 完善的信号机控制器表实体
 * 完全符合GA/T 1049协议SignalController定义
 */
@Entity
@Table(name = "gat_signal_controller",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_signal_controller_id", columnNames = "signalControllerId"),
                @UniqueConstraint(name = "uk_identifier", columnNames = "identifier")
        },
        indexes = {
                @Index(name = "idx_supplier", columnList = "supplier"),
                @Index(name = "idx_device_type", columnList = "deviceType"),
                @Index(name = "idx_comm_mode", columnList = "commMode"),
                @Index(name = "idx_ip_address", columnList = "ipAddress"),
                @Index(name = "idx_device_status", columnList = "deviceStatus"),
                @Index(name = "idx_connection_status", columnList = "connectionStatus"),
                @Index(name = "idx_location", columnList = "longitude, latitude"),
                @Index(name = "idx_adapter_type", columnList = "adapterType"),
                @Index(name = "idx_sync_status", columnList = "syncStatus")
        })
public class GatSignalControllerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================================================
    // GA/T 1049协议必需字段 (与SignalController.java完全对应)
    // ========================================================================

    /**
     * 信号机设备编号 (协议：signalControllerID)
     * 格式：6位机构代码+99+4位数字
     */
    @NotBlank(message = "信号机设备编号不能为空")
    @Pattern(regexp = "\\d{6}99\\d{4}", message = "信号机设备编号格式错误，应为6位机构代码+99+4位数字")
    @Column(name = "signal_controller_id", length = 12, nullable = false, unique = true)
    private String signalControllerId;

    /**
     * 供应商 (协议：supplier)
     */
    @NotBlank(message = "供应商不能为空")
    @Column(name = "supplier", length = 64, nullable = false)
    private String supplier;

    /**
     * 规格型号 (协议：type)
     */
    @NotBlank(message = "规格型号不能为空")
    @Size(min = 1, max = 16, message = "规格型号长度应为1-16位")
    @Column(name = "device_type", length = 16, nullable = false)
    private String deviceType;

    /**
     * 识别码 (协议：id/identifier)
     */
    @NotBlank(message = "识别码不能为空")
    @Size(min = 1, max = 16, message = "识别码长度应为1-16位")
    @Column(name = "identifier", length = 16, nullable = false, unique = true)
    private String identifier;

    /**
     * 版本信息 (协议：version)
     */
    @Column(name = "version", length = 32)
    private String version = "1.0";

    /**
     * 描述信息 (协议：description)
     */
    @Column(name = "description", length = 256)
    private String description;

    // ========================================================================
    // 通信配置字段
    // ========================================================================

    /**
     * 通信方式 (协议：commMode)
     * 1-TCP_CLIENT，2-TCP_SERVER，3-UDP，4-SERIAL，5-HTTP
     */
    @NotNull(message = "通信方式不能为空")
    @Min(value = 1, message = "通信方式值不能小于1")
    @Max(value = 99, message = "通信方式值不能大于99")
    @Column(name = "comm_mode", nullable = false)
    private Integer commMode = 1;

    /**
     * IP地址 (协议：ip)
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * 端口号 (协议：port)
     */
    @Min(value = 1, message = "端口号不能小于1")
    @Max(value = 65535, message = "端口号不能大于65535")
    @Column(name = "port")
    private Integer port;

    /**
     * 子网掩码 (协议：subMask)
     */
    @Column(name = "subnet_mask", length = 15)
    private String subnetMask;

    /**
     * 网关地址 (协议：gateway)
     */
    @Column(name = "gateway", length = 45)
    private String gateway;

    // ========================================================================
    // 位置和状态信息
    // ========================================================================

    /**
     * 经度 (协议：longitude)
     */
    @DecimalMin(value = "-180.0", message = "经度不能小于-180")
    @DecimalMax(value = "180.0", message = "经度不能大于180")
    @Column(name = "longitude", precision = 10, scale = 7)
    private Double longitude;

    /**
     * 纬度 (协议：latitude)
     */
    @DecimalMin(value = "-90.0", message = "纬度不能小于-90")
    @DecimalMax(value = "90.0", message = "纬度不能大于90")
    @Column(name = "latitude", precision = 10, scale = 7)
    private Double latitude;

    /**
     * 门状态 (协议：hasDoorStatus)
     */
    @Column(name = "has_door_status")
    private Integer hasDoorStatus = 0;

    // ========================================================================
    // 复杂对象字段 (使用JSON存储)
    // ========================================================================

    /**
     * 控制路口列表 (协议：crossIDList，必需字段)
     */
    @NotNull(message = "控制路口列表不能为空")
    @Type(type = "json")
    @Column(name = "cross_id_list", columnDefinition = "JSON", nullable = false)
    private String crossIdList;

    /**
     * 灯组列表 (协议：lampGroupList)
     */
    @Type(type = "json")
    @Column(name = "lamp_group_list", columnDefinition = "JSON")
    private String lampGroupList;

    /**
     * 检测器列表 (协议：detectorList)
     */
    @Type(type = "json")
    @Column(name = "detector_list", columnDefinition = "JSON")
    private String detectorList;

    /**
     * 通信地址对象 (协议：commAddress)
     */
    @Type(type = "json")
    @Column(name = "comm_address", columnDefinition = "JSON")
    private String commAddress;

    /**
     * 设备能力集 (协议：capabilitySet)
     */
    @Type(type = "json")
    @Column(name = "capability_set", columnDefinition = "JSON")
    private String capabilitySet;

    /**
     * 自定义参数 (协议：customParams)
     */
    @Type(type = "json")
    @Column(name = "custom_params", columnDefinition = "JSON")
    private String customParams;

    // ========================================================================
    // 管理和状态字段 (数据库管理需要，不在协议中)
    // ========================================================================

    /**
     * 设备名称 (用于管理显示)
     */
    @Column(name = "controller_name", length = 128)
    private String controllerName;

    /**
     * 安装位置 (详细地址)
     */
    @Column(name = "installation_location", length = 256)
    private String installationLocation;

    /**
     * 设备状态：0-离线，1-在线，2-故障，3-维护
     */
    @Column(name = "device_status")
    private Integer deviceStatus = 0;

    /**
     * 连接状态：0-未连接，1-已连接，2-连接异常
     */
    @Column(name = "connection_status")
    private Integer connectionStatus = 0;

    /**
     * 最后在线时间
     */
    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    /**
     * 固件版本
     */
    @Column(name = "firmware_version", length = 32)
    private String firmwareVersion;

    /**
     * 协议版本
     */
    @Column(name = "protocol_version", length = 16)
    private String protocolVersion = "GA/T1049-2016";

    /**
     * 适配器类型：HISENSE/EHUALU/GENERIC
     */
    @Column(name = "adapter_type", length = 32)
    private String adapterType;

    /**
     * 配置版本号，用于同步控制
     */
    @Column(name = "config_version")
    private Long configVersion = 1L;

    /**
     * 同步状态：0-未同步，1-同步中，2-已同步，3-同步失败
     */
    @Column(name = "sync_status")
    private Integer syncStatus = 0;

    /**
     * 记录状态：0-禁用，1-启用
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * 设备安装日期
     */
    @Column(name = "installation_date")
    private LocalDate installationDate;

    /**
     * 最后维护日期
     */
    @Column(name = "maintenance_date")
    private LocalDate maintenanceDate;

    /**
     * 设备保修期截止日期
     */
    @Column(name = "warranty_expiry_date")
    private LocalDate warrantyExpiryDate;

    // ========================================================================
    // 审计字段
    // ========================================================================

    @Column(name = "created_by", length = 64)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by", length = 64)
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========================================================================
    // 关联关系
    // ========================================================================

    @OneToMany(mappedBy = "controller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GatSyncTaskEntity> syncTasks;

    @OneToMany(mappedBy = "controller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GatEventEntity> events;

    // ========================================================================
    // JPA生命周期方法
    // ========================================================================

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        // 设置默认值
        if (this.version == null) {
            this.version = "1.0";
        }
        if (this.protocolVersion == null) {
            this.protocolVersion = "GA/T1049-2016";
        }
        if (this.configVersion == null) {
            this.configVersion = 1L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        // 更新时自动增加配置版本号
        if (this.configVersion != null) {
            this.configVersion++;
        }
    }

    // ========================================================================
    // 构造方法
    // ========================================================================

    public GatSignalControllerEntity() {}

    public GatSignalControllerEntity(String signalControllerId, String supplier,
                                     String deviceType, String identifier) {
        this.signalControllerId = signalControllerId;
        this.supplier = supplier;
        this.deviceType = deviceType;
        this.identifier = identifier;
    }

    // ========================================================================
    // 便利方法
    // ========================================================================

    /**
     * 获取通信模式枚举值
     */
    public CommMode getCommModeEnum() {
        return CommMode.fromCode(String.valueOf(this.commMode));
    }

    /**
     * 设置通信模式枚举值
     */
    public void setCommModeEnum(CommMode commMode) {
        this.commMode = Integer.valueOf(commMode.getCode(), 10);
    }

    /**
     * 检查设备是否在线
     */
    public boolean isOnline() {
        return Integer.valueOf(1).equals(this.deviceStatus);
    }

    /**
     * 检查设备是否已连接
     */
    public boolean isConnected() {
        return Integer.valueOf(1).equals(this.connectionStatus);
    }

    /**
     * 检查设备是否有故障
     */
    public boolean hasFault() {
        return Integer.valueOf(2).equals(this.deviceStatus);
    }

    /**
     * 检查配置是否需要同步
     */
    public boolean needSync() {
        return !Integer.valueOf(2).equals(this.syncStatus);
    }

    // ========================================================================
    // Getter and Setter 方法
    // ========================================================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSignalControllerId() { return signalControllerId; }
    public void setSignalControllerId(String signalControllerId) { this.signalControllerId = signalControllerId; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCommMode() { return commMode; }
    public void setCommMode(Integer commMode) { this.commMode = commMode; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }

    public String getSubnetMask() { return subnetMask; }
    public void setSubnetMask(String subnetMask) { this.subnetMask = subnetMask; }

    public String getGateway() { return gateway; }
    public void setGateway(String gateway) { this.gateway = gateway; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Integer getHasDoorStatus() { return hasDoorStatus; }
    public void setHasDoorStatus(Integer hasDoorStatus) { this.hasDoorStatus = hasDoorStatus; }

    public String getCrossIdList() { return crossIdList; }
    public void setCrossIdList(String crossIdList) { this.crossIdList = crossIdList; }

    public String getLampGroupList() { return lampGroupList; }
    public void setLampGroupList(String lampGroupList) { this.lampGroupList = lampGroupList; }

    public String getDetectorList() { return detectorList; }
    public void setDetectorList(String detectorList) { this.detectorList = detectorList; }

    public String getCommAddress() { return commAddress; }
    public void setCommAddress(String commAddress) { this.commAddress = commAddress; }

    public String getCapabilitySet() { return capabilitySet; }
    public void setCapabilitySet(String capabilitySet) { this.capabilitySet = capabilitySet; }

    public String getCustomParams() { return customParams; }
    public void setCustomParams(String customParams) { this.customParams = customParams; }

    public String getControllerName() { return controllerName; }
    public void setControllerName(String controllerName) { this.controllerName = controllerName; }

    public String getInstallationLocation() { return installationLocation; }
    public void setInstallationLocation(String installationLocation) { this.installationLocation = installationLocation; }

    public Integer getDeviceStatus() { return deviceStatus; }
    public void setDeviceStatus(Integer deviceStatus) { this.deviceStatus = deviceStatus; }

    public Integer getConnectionStatus() { return connectionStatus; }
    public void setConnectionStatus(Integer connectionStatus) { this.connectionStatus = connectionStatus; }

    public LocalDateTime getLastOnlineTime() { return lastOnlineTime; }
    public void setLastOnlineTime(LocalDateTime lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }

    public String getFirmwareVersion() { return firmwareVersion; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }

    public String getProtocolVersion() { return protocolVersion; }
    public void setProtocolVersion(String protocolVersion) { this.protocolVersion = protocolVersion; }

    public String getAdapterType() { return adapterType; }
    public void setAdapterType(String adapterType) { this.adapterType = adapterType; }

    public Long getConfigVersion() { return configVersion; }
    public void setConfigVersion(Long configVersion) { this.configVersion = configVersion; }

    public Integer getSyncStatus() { return syncStatus; }
    public void setSyncStatus(Integer syncStatus) { this.syncStatus = syncStatus; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDate getInstallationDate() { return installationDate; }
    public void setInstallationDate(LocalDate installationDate) { this.installationDate = installationDate; }

    public LocalDate getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(LocalDate maintenanceDate) { this.maintenanceDate = maintenanceDate; }

    public LocalDate getWarrantyExpiryDate() { return warrantyExpiryDate; }
    public void setWarrantyExpiryDate(LocalDate warrantyExpiryDate) { this.warrantyExpiryDate = warrantyExpiryDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<GatSyncTaskEntity> getSyncTasks() { return syncTasks; }
    public void setSyncTasks(List<GatSyncTaskEntity> syncTasks) { this.syncTasks = syncTasks; }

    public List<GatEventEntity> getEvents() { return events; }
    public void setEvents(List<GatEventEntity> events) { this.events = events; }

    // ========================================================================
    // toString, equals, hashCode 方法
    // ========================================================================

    @Override
    public String toString() {
        return "GatSignalControllerEntity{" +
                "id=" + id +
                ", signalControllerId='" + signalControllerId + '\'' +
                ", supplier='" + supplier + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", identifier='" + identifier + '\'' +
                ", version='" + version + '\'' +
                ", commMode=" + commMode +
                ", ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", deviceStatus=" + deviceStatus +
                ", connectionStatus=" + connectionStatus +
                ", adapterType='" + adapterType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GatSignalControllerEntity)) return false;
        GatSignalControllerEntity that = (GatSignalControllerEntity) o;
        return signalControllerId != null && signalControllerId.equals(that.signalControllerId);
    }

    @Override
    public int hashCode() {
        return signalControllerId != null ? signalControllerId.hashCode() : 0;
    }
}