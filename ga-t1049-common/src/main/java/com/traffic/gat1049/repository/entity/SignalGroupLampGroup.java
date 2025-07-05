package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 14. 信号组灯组关联表 (SignalGroupLampGroup)
// ================================
@Data
@Accessors(chain = true)
@TableName("signal_group_lamp_group")
@Entity
@Table(name = "signal_group_lamp_group")
public class SignalGroupLampGroup {
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

    @TableField("lamp_group_no")
    @Column(name = "lamp_group_no", nullable = false)
    private Integer lampGroupNo;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
