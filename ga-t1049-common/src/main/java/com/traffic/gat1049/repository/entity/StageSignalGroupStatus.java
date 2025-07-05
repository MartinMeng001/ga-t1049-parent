package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 16. 阶段信号组状态表 (StageSignalGroupStatus)
// ================================
@Data
@Accessors(chain = true)
@TableName("stage_signal_group_status")
@Entity
@Table(name = "stage_signal_group_status")
public class StageSignalGroupStatus {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("stage_no")
    @Column(name = "stage_no", nullable = false)
    private Integer stageNo;

    @TableField("signal_group_no")
    @Column(name = "signal_group_no", nullable = false)
    private Integer signalGroupNo;

    @TableField("lamp_status")
    @Column(name = "lamp_status", length = 3, nullable = false)
    private String lampStatus;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
