package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 1. 系统参数表 (SysInfo)
// ================================
@Data
@Accessors(chain = true)
@TableName("sys_info")
@Entity
@Table(name = "sys_info")
public class SysInfoEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("system_id")
    @Column(name = "system_id", length = 20, nullable = false, unique = true)
    private String systemId;

    @TableField("sys_name")
    @Column(name = "sys_name", length = 50, nullable = false)
    private String sysName;

    @TableField("sys_version")
    @Column(name = "sys_version", length = 10, nullable = false)
    private String sysVersion;

    @TableField("supplier")
    @Column(name = "supplier", length = 50, nullable = false)
    private String supplier;

    @TableField("is_active")
    @Column(name = "is_active")
    private Boolean isActive = true;

    @TableField("description")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
