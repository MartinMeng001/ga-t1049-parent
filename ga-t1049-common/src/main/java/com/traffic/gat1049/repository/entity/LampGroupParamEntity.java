package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 12. 信号灯组参数表 (LampGroupParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("lamp_group_param")
@Entity
@Table(name = "lamp_group_param")
public class LampGroupParamEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("lamp_group_no")
    @Column(name = "lamp_group_no", nullable = false)
    private Integer lampGroupNo;

    @TableField("lamp_group_name")
    @Column(name = "lamp_group_name", length = 20)
    private String lampGroupName;

    @TableField("lamp_group_type")
    @Column(name = "lamp_group_type", nullable = false)
    private Integer lampGroupType;

    @TableField("direction_no")
    @Column(name = "direction_no", nullable = false)
    private Integer directionNo;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
