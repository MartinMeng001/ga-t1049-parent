package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 17. 配时方案参数表 (PlanParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("plan_param")
@Entity
@Table(name = "plan_param")
public class PlanParamEntity {
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

    @TableField("plan_name")
    @Column(name = "plan_name", length = 50)
    private String planName;

    @TableField("cycle_len")
    @Column(name = "cycle_len", nullable = false)
    private Integer cycleLen;

    @TableField("coord_stage_no")
    @Column(name = "coord_stage_no")
    private Integer coordStageNo = 0;

    @TableField("offset")
    @Column(name = "offset")
    private Integer offset = 0;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
