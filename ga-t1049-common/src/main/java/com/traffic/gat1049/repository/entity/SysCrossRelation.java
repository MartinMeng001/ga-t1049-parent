package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 36. 系统路口关系表 (SysCrossRelation)
@Data
@Accessors(chain = true)
@TableName("sys_cross_relation")
@Entity
@Table(name = "sys_cross_relation")
public class SysCrossRelation {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("system_id")
    @Column(name = "system_id", length = 20, nullable = false)
    private String systemId;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("is_primary")
    @Column(name = "is_primary")
    private Boolean isPrimary = false;

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
