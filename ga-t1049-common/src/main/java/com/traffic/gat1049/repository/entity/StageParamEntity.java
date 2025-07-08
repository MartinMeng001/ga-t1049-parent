package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 15. 阶段参数表 (StageParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("stage_param")
@Entity
@Table(name = "stage_param")
public class StageParamEntity {
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

    @TableField("stage_name")
    @Column(name = "stage_name", length = 50)
    private String stageName;

    @TableField("attribute")
    @Column(name = "attribute")
    private Integer attribute = 0;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
