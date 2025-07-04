package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 数据字典表实体
 */
@Entity
@Table(name = "gat_data_dictionary", indexes = {
        @Index(name = "idx_dict_code", columnList = "dictCode"),
        @Index(name = "idx_dict_type", columnList = "dictType"),
        @Index(name = "idx_parent_code", columnList = "parentCode")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_dict_type_value", columnNames = {"dictType", "dictValue"})
})
public class GatDataDictionaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dict_code", length = 32, nullable = false)
    private String dictCode;

    @Column(name = "dict_name", length = 64, nullable = false)
    private String dictName;

    @Column(name = "dict_type", length = 32, nullable = false)
    private String dictType;

    @Column(name = "dict_value", length = 128, nullable = false)
    private String dictValue;

    @Column(name = "dict_label", length = 128, nullable = false)
    private String dictLabel;

    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder;

    @Column(name = "parent_code", length = 32)
    private String parentCode;

    @Column(name = "is_default", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isDefault; // 0-否，1-是

    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status; // 0-禁用，1-启用

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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
    public GatDataDictionaryEntity() {}

    public GatDataDictionaryEntity(String dictCode, String dictName, String dictType, String dictValue, String dictLabel) {
        this.dictCode = dictCode;
        this.dictName = dictName;
        this.dictType = dictType;
        this.dictValue = dictValue;
        this.dictLabel = dictLabel;
        this.sortOrder = 0;
        this.isDefault = 0;
        this.status = 1;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDictCode() { return dictCode; }
    public void setDictCode(String dictCode) { this.dictCode = dictCode; }

    public String getDictName() { return dictName; }
    public void setDictName(String dictName) { this.dictName = dictName; }

    public String getDictType() { return dictType; }
    public void setDictType(String dictType) { this.dictType = dictType; }

    public String getDictValue() { return dictValue; }
    public void setDictValue(String dictValue) { this.dictValue = dictValue; }

    public String getDictLabel() { return dictLabel; }
    public void setDictLabel(String dictLabel) { this.dictLabel = dictLabel; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getParentCode() { return parentCode; }
    public void setParentCode(String parentCode) { this.parentCode = parentCode; }

    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "GatDataDictionaryEntity{" +
                "id=" + id +
                ", dictCode='" + dictCode + '\'' +
                ", dictName='" + dictName + '\'' +
                ", dictType='" + dictType + '\'' +
                ", dictValue='" + dictValue + '\'' +
                ", dictLabel='" + dictLabel + '\'' +
                ", status=" + status +
                '}';
    }
}
