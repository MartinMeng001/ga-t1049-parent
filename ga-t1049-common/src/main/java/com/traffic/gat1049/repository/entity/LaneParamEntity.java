package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 10. 车道参数表 (LaneParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("lane_param")
@Entity
@Table(name = "lane_param")
public class LaneParamEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("direction_no")
    @Column(name = "direction_no", nullable = false)
    private Integer directionNo;

    @TableField("lane_no")
    @Column(name = "lane_no", nullable = false)
    private Integer laneNo;

    @TableField("lane_name")
    @Column(name = "lane_name", length = 20)
    private String laneName;

    @TableField("movement")
    @Column(name = "movement", nullable = false)
    private Integer movement;

    @TableField("width")
    @Column(name = "width", precision = 4, scale = 2)
    private Double width;

    @TableField("is_import")
    @Column(name = "is_import")
    private Boolean isImport = true;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
