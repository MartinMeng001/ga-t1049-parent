package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 系统信息表实体
 */
@Entity
@Table(name = "gat_sys_info", indexes = {
        @Index(name = "idx_system_id", columnList = "systemId"),
        @Index(name = "idx_status", columnList = "status")
})
public class GatSysInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_id", length = 64, nullable = false, unique = true)
    private String systemId;

    @Column(name = "system_name", length = 128, nullable = false)
    private String systemName;

    @Column(name = "software_version", length = 32)
    private String softwareVersion;

    @Column(name = "hardware_version", length = 32)
    private String hardwareVersion;

    @Column(name = "manufacturer", length = 128)
    private String manufacturer;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @Column(name = "location", length = 256)
    private String location;

    @Column(name = "contact_info", length = 256)
    private String contactInfo;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status; // 0-禁用，1-启用

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
    public GatSysInfoEntity() {}

    public GatSysInfoEntity(String systemId, String systemName) {
        this.systemId = systemId;
        this.systemName = systemName;
        this.status = 1;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSystemId() { return systemId; }
    public void setSystemId(String systemId) { this.systemId = systemId; }

    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }

    public String getSoftwareVersion() { return softwareVersion; }
    public void setSoftwareVersion(String softwareVersion) { this.softwareVersion = softwareVersion; }

    public String getHardwareVersion() { return hardwareVersion; }
    public void setHardwareVersion(String hardwareVersion) { this.hardwareVersion = hardwareVersion; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public LocalDate getInstallationDate() { return installationDate; }
    public void setInstallationDate(LocalDate installationDate) { this.installationDate = installationDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "GatSysInfoEntity{" +
                "id=" + id +
                ", systemId='" + systemId + '\'' +
                ", systemName='" + systemName + '\'' +
                ", status=" + status +
                '}';
    }
}