package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 系统关联关系表 (支持多系统架构)
// ================================

// 33. 系统区域关系表 (SysRegionRelation)
@Data
@Accessors(chain = true)
@TableName("sys_region_relation")
@Entity
@Table(name = "sys_region_relation")
public class SysRegionRelationEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("system_id")
    @Column(name = "system_id", length = 20, nullable = false)
    private String systemId;

    @TableField("region_id")
    @Column(name = "region_id", length = 9, nullable = false)
    private String regionId;

    @TableField("is_active")
    @Column(name = "is_active")
    private Boolean isActive = true;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
