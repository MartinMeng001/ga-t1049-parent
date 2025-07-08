package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 18. 阶段配时参数表 (StageTimingParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("stage_timing_param")
@Entity
@Table(name = "stage_timing_param")
public class StageTimingParamEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("plan_no")
    @Column(name = "plan_no", nullable = false)
    private Integer planNo;

    @TableField("stage_no")
    @Column(name = "stage_no", nullable = false)
    private Integer stageNo;

    @TableField("green_time")
    @Column(name = "green_time", nullable = false)
    private Integer greenTime;

    @TableField("yellow_time")
    @Column(name = "yellow_time", nullable = false)
    private Integer yellowTime;

    @TableField("red_time")
    @Column(name = "red_time", nullable = false)
    private Integer redTime;

    @TableField("min_green")
    @Column(name = "min_green")
    private Integer minGreen;

    @TableField("max_green")
    @Column(name = "max_green")
    private Integer maxGreen;

    @TableField("extend_green")
    @Column(name = "extend_green")
    private Integer extendGreen;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
