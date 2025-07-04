package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 系统配置表实体
 */
@Entity
@Table(name = "gat_system_config", indexes = {
        @Index(name = "idx_config_key", columnList = "configKey"),
        @Index(name = "idx_config_group", columnList = "configGroup")
})
public class GatSystemConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", length = 64, nullable = false, unique = true)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "config_type", length = 32, columnDefinition = "VARCHAR(32) DEFAULT 'STRING'")
    private String configType; // STRING/NUMBER/BOOLEAN/JSON

    @Column(name = "config_group", length = 32)
    private String configGroup;

    @Column(name = "config_name", length = 128)
    private String configName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_encrypted", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isEncrypted; // 0-否，1-是

    @Column(name = "is_readonly", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isReadonly; // 0-否，1-是

    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (configType == null) {
            configType = "STRING";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public GatSystemConfigEntity() {}

    public GatSystemConfigEntity(String configKey, String configValue, String configType, String configGroup) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.configType = configType;
        this.configGroup = configGroup;
        this.isEncrypted = 0;
        this.isReadonly = 0;
        this.sortOrder = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }

    public String getConfigType() { return configType; }
    public void setConfigType(String configType) { this.configType = configType; }

    public String getConfigGroup() { return configGroup; }
    public void setConfigGroup(String configGroup) { this.configGroup = configGroup; }

    public String getConfigName() { return configName; }
    public void setConfigName(String configName) { this.configName = configName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getIsEncrypted() { return isEncrypted; }
    public void setIsEncrypted(Integer isEncrypted) { this.isEncrypted = isEncrypted; }

    public Integer getIsReadonly() { return isReadonly; }
    public void setIsReadonly(Integer isReadonly) { this.isReadonly = isReadonly; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}