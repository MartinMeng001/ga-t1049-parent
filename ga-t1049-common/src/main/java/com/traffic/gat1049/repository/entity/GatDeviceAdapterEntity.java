package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 设备适配器配置表实体
 */
@Entity
@Table(name = "gat_device_adapter", indexes = {
        @Index(name = "idx_adapter_id", columnList = "adapterId"),
        @Index(name = "idx_vendor", columnList = "vendor"),
        @Index(name = "idx_adapter_type", columnList = "adapterType")
})
public class GatDeviceAdapterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "adapter_id", length = 32, nullable = false, unique = true)
    private String adapterId;

    @Column(name = "adapter_name", length = 64, nullable = false)
    private String adapterName;

    @Column(name = "vendor", length = 32, nullable = false)
    private String vendor; // 海信/易华录/其他

    @Column(name = "adapter_type", length = 32, nullable = false)
    private String adapterType;

    @Column(name = "adapter_version", length = 16)
    private String adapterVersion;

    @Column(name = "protocol_type", length = 32)
    private String protocolType;

    @Column(name = "communication_params", columnDefinition = "JSON")
    private String communicationParams;

    @Column(name = "config_template", columnDefinition = "JSON")
    private String configTemplate;

    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status; // 0-禁用，1-启用

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 一对多关系
    @OneToMany(mappedBy = "adapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GatDeviceAdapterInstanceEntity> instances;

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
    public GatDeviceAdapterEntity() {}

    public GatDeviceAdapterEntity(String adapterId, String adapterName, String vendor, String adapterType) {
        this.adapterId = adapterId;
        this.adapterName = adapterName;
        this.vendor = vendor;
        this.adapterType = adapterType;
        this.status = 1;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdapterId() { return adapterId; }
    public void setAdapterId(String adapterId) { this.adapterId = adapterId; }

    public String getAdapterName() { return adapterName; }
    public void setAdapterName(String adapterName) { this.adapterName = adapterName; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public String getAdapterType() { return adapterType; }
    public void setAdapterType(String adapterType) { this.adapterType = adapterType; }

    public String getAdapterVersion() { return adapterVersion; }
    public void setAdapterVersion(String adapterVersion) { this.adapterVersion = adapterVersion; }

    public String getProtocolType() { return protocolType; }
    public void setProtocolType(String protocolType) { this.protocolType = protocolType; }

    public String getCommunicationParams() { return communicationParams; }
    public void setCommunicationParams(String communicationParams) { this.communicationParams = communicationParams; }

    public String getConfigTemplate() { return configTemplate; }
    public void setConfigTemplate(String configTemplate) { this.configTemplate = configTemplate; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<GatDeviceAdapterInstanceEntity> getInstances() { return instances; }
    public void setInstances(List<GatDeviceAdapterInstanceEntity> instances) { this.instances = instances; }
}