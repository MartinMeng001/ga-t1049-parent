package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 32. 路口信号机关系表 (CrossSignalController)
@Data
@Accessors(chain = true)
@TableName("cross_signal_controller")
@Entity
@Table(name = "cross_signal_controller")
public class CrossSignalController {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("signal_controller_id")
    @Column(name = "signal_controller_id", length = 14, nullable = false)
    private String signalControllerId;

    @TableField("is_master")
    @Column(name = "is_master")
    private Boolean isMaster = true;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
