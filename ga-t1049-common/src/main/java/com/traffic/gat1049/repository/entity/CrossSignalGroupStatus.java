package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 23. 信号组灯色状态表 (CrossSignalGroupStatus)
@Data
@Accessors(chain = true)
@TableName("cross_signal_group_status")
@Entity
@Table(name = "cross_signal_group_status")
public class CrossSignalGroupStatus {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("signal_group_no")
    @Column(name = "signal_group_no", nullable = false)
    private Integer signalGroupNo;

    @TableField("lamp_status")
    @Column(name = "lamp_status", length = 3, nullable = false)
    private String lampStatus;

    @TableField("lamp_status_time")
    @Column(name = "lamp_status_time", nullable = false)
    private LocalDateTime lampStatusTime;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
