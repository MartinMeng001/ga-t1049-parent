package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 13. 信号组车道关联表 (SignalGroupLane)
// ================================
@Data
@Accessors(chain = true)
@TableName("signal_group_lane")
@Entity
@Table(name = "signal_group_lane")
public class SignalGroupLaneEntity {
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

    @TableField("direction_no")
    @Column(name = "direction_no", nullable = false)
    private Integer directionNo;

    @TableField("lane_no")
    @Column(name = "lane_no", nullable = false)
    private Integer laneNo;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
