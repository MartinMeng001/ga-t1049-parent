package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 阶段交通流数据表实体类 (StageTrafficData)
 * 根据gat1049.sql中的实际表结构修正
 */
@Data
@Accessors(chain = true)
@TableName("stage_traffic_data")
@Entity
@Table(name = "stage_traffic_data")
public class StageTrafficDataEntity {

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
     * 阶段开始时间
     */
    @TableField("start_time")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 阶段结束时间
     */
    @TableField("end_time")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * 阶段号
     */
    @TableField("stage_no")
    @Column(name = "stage_no", nullable = false)
    private Integer stageNo;

    /**
     * 车道序号
     */
    @TableField("lane_no")
    @Column(name = "lane_no", nullable = false)
    private Integer laneNo;

    /**
     * 过车数量(辆)
     */
    @TableField("vehicle_num")
    @Column(name = "vehicle_num")
    private Integer vehicleNum;

    /**
     * 小客车当量(pcu/小时)
     */
    @TableField("pcu")
    @Column(name = "pcu")
    private Integer pcu;

    /**
     * 平均车头时距(秒/辆)
     */
    @TableField("head_time")
    @Column(name = "head_time")
    private Integer headTime;

    /**
     * 饱和度(0-100)
     */
    @TableField("saturation")
    @Column(name = "saturation")
    private Integer saturation;

    /**
     * 阶段结束时排队长度(米)
     */
    @TableField("queue_length")
    @Column(name = "queue_length")
    private Integer queueLength;

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