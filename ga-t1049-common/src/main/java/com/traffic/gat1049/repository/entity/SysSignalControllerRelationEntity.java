package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 37. 系统信号机关系表 (SysSignalControllerRelation)
@Data
@Accessors(chain = true)
@TableName("sys_signal_controller_relation")
@Entity
@Table(name = "sys_signal_controller_relation")
public class SysSignalControllerRelationEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("system_id")
    @Column(name = "system_id", length = 20, nullable = false)
    private String systemId;

    @TableField("signal_controller_id")
    @Column(name = "signal_controller_id", length = 14, nullable = false)
    private String signalControllerId;

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
