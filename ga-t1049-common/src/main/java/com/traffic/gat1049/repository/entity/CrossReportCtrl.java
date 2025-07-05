package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 41. 数据上传控制表 (CrossReportCtrl)
@Data
@Accessors(chain = true)
@TableName("cross_report_ctrl")
@Entity
@Table(name = "cross_report_ctrl")
public class CrossReportCtrl {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("data_type")
    @Column(name = "data_type", length = 30, nullable = false)
    private String dataType;

    @TableField("cmd")
    @Enumerated(EnumType.STRING)
    @Column(name = "cmd", nullable = false)
    private ControlCommand cmd;

    @TableField("status")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ControlStatus status = ControlStatus.ACTIVE;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    public enum ControlCommand {
        Start, Stop
    }

    public enum ControlStatus {
        ACTIVE, INACTIVE
    }
}
