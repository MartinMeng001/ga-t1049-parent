package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 路口交通流数据表实体类 (CrossTrafficData)
 * 根据gat1049.sql中的实际表结构修正
 */
@Data
@Accessors(chain = true)
@TableName("cross_traffic_data")
@Entity
@Table(name = "cross_traffic_data")
public class CrossTrafficDataEntity {

    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 路口编号
     */
    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    /**
     * 统计截止时间
     */
    @TableField("end_time")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * 时间间隔(秒)
     */
    @TableField("interval_seconds")
    @Column(name = "interval_seconds", nullable = false)
    private Integer intervalSeconds;

    /**
     * 车道序号
     */
    @TableField("lane_no")
    @Column(name = "lane_no", nullable = false)
    private Integer laneNo;

    /**
     * 交通流量(辆/小时)
     */
    @TableField("volume")
    @Column(name = "volume")
    private Integer volume;

    /**
     * 平均车长(厘米)
     */
    @TableField("avg_veh_len")
    @Column(name = "avg_veh_len")
    private Integer avgVehLen;

    /**
     * 小客车当量(pcu/小时)
     */
    @TableField("pcu")
    @Column(name = "pcu")
    private Integer pcu;

    /**
     * 平均车头间距(厘米/辆)
     */
    @TableField("head_distance")
    @Column(name = "head_distance")
    private Integer headDistance;

    /**
     * 平均车头时距(秒/辆)
     */
    @TableField("head_time")
    @Column(name = "head_time")
    private Integer headTime;

    /**
     * 平均速度(公里/小时)
     */
    @TableField("speed")
    @Column(name = "speed")
    private Float speed;

    /**
     * 饱和度(0-100)
     */
    @TableField("saturation")
    @Column(name = "saturation")
    private Integer saturation;

    /**
     * 平均密度(辆/公里)
     */
    @TableField("density")
    @Column(name = "density")
    private Integer density;

    /**
     * 平均排队长度(米)
     */
    @TableField("queue_length")
    @Column(name = "queue_length")
    private Integer queueLength;

    /**
     * 统计周期内最大排队长度(米)
     */
    @TableField("max_queue_length")
    @Column(name = "max_queue_length")
    private Integer maxQueueLength;

    /**
     * 占有率(0-100)
     */
    @TableField("occupancy")
    @Column(name = "occupancy")
    private Integer occupancy;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}