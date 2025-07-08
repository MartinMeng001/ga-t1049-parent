package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 22. 路口阶段表 (CrossStage)
@Data
@Accessors(chain = true)
@TableName("cross_stage")
@Entity
@Table(name = "cross_stage")
public class CrossStageEntity {
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

    @TableField("start_time")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @TableField("stage_len")
    @Column(name = "stage_len", nullable = false)
    private Integer stageLen;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
