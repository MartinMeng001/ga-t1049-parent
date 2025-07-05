package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 24. 路口交通流数据表 (CrossTrafficData)
@Data
@Accessors(chain = true)
@TableName("cross_traffic_data")
@Entity
@Table(name = "cross_traffic_data")
public class CrossTrafficData {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("direction_no")
    @Column(name = "direction_no", nullable = false)
    private Integer directionNo;

    @TableField("lane_no")
    @Column(name = "lane_no", nullable = false)
    private Integer laneNo;

    @TableField("start_time")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @TableField("end_time")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @TableField("volume")
    @Column(name = "volume")
    private Integer volume = 0;

    @TableField("occupancy")
    @Column(name = "occupancy", precision = 5, scale = 2)
    private Double occupancy = 0.00;

    @TableField("speed")
    @Column(name = "speed", precision = 5, scale = 2)
    private Double speed = 0.00;

    @TableField("headway")
    @Column(name = "headway", precision = 6, scale = 2)
    private Double headway = 0.00;

    @TableField("interval_len")
    @Column(name = "interval_len", nullable = false)
    private Integer intervalLen;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
