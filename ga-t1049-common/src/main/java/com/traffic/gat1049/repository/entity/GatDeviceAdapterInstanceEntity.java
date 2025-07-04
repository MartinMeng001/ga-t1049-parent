package com.traffic.gat1049.repository.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 设备适配器实例表实体
 */
@Entity
@Table(name = "gat_device_adapter_instance", indexes = {
        @Index(name = "idx_instance_id", columnList = "instanceId"),
        @Index(name = "idx_adapter_id", columnList = "adapterId"),
        @Index(name = "idx_controller_id", columnList = "controllerId"),
        @Index(name = "idx_sync_status", columnList = "syncStatus")
})
public class GatDeviceAdapterInstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instance_id", length = 32, nullable = false, unique = true)
    private String instanceId;

    @Column(name = "adapter_id", length = 32, nullable = false)
    private String adapterId;

    @Column(name = "controller_id", length = 12, nullable = false)
    private String controllerId;

    @Column(name = "instance_name", length = 64)
    private String instanceName;

    @Column(name = "config_params", columnDefinition = "JSON")
    private String configParams;

    @Column(name = "connection_params", columnDefinition = "JSON")
    private String connectionParams;

    @Column(name = "sync_interval", columnDefinition = "INT DEFAULT 30")
    private Integer syncInterval; // 同步间隔(秒)

    @Column(name = "retry_count", columnDefinition = "INT DEFAULT 3")
    private Integer retryCount;

    @Column(name = "timeout_seconds", columnDefinition = "INT DEFAULT 10")
    private Integer timeoutSeconds;

    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;

    @Column(name = "sync_status", columnDefinition = "TINYINT DEFAULT 0")
    private Integer syncStatus; // 0-未同步，1-同步中，2-同步成功，3-同步失败

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status; // 0-禁用，1-启用

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 多对一关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adapter_id", referencedColumnName = "adapterId", insertable = false, updatable = false)
    private GatDeviceAdapterEntity adapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_id", referencedColumnName = "controllerId", insertable = false, updatable = false)
    private GatSignalControllerEntity controller;

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
    public GatDeviceAdapterInstanceEntity() {}

    public GatDeviceAdapterInstanceEntity(String instanceId, String adapterId, String controllerId) {
        this.instanceId = instanceId;
        this.adapterId = adapterId;
        this.controllerId = controllerId;
        this.syncInterval = 30;
        this.retryCount = 3;
        this.timeoutSeconds = 10;
        this.syncStatus = 0;
        this.status = 1;
    }

    // Getters and Setters (省略，按照标准模式实现)
    // ... 所有字段的getter和setter方法
}