package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 20. 检测器参数表 (DetectorParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("detector_param")
@Entity
@Table(name = "detector_param")
public class DetectorParamEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("detector_id")
    @Column(name = "detector_id", length = 20, nullable = false)
    private String detectorId;

    @TableField("detector_name")
    @Column(name = "detector_name", length = 50)
    private String detectorName;

    @TableField("detector_type")
    @Column(name = "detector_type", nullable = false)
    private Integer detectorType;

    @TableField("direction_no")
    @Column(name = "direction_no", nullable = false)
    private Integer directionNo;

    @TableField("lane_no")
    @Column(name = "lane_no")
    private Integer laneNo;

    @TableField("distance")
    @Column(name = "distance", precision = 6, scale = 2)
    private Double distance;

    @TableField("longitude")
    @Column(name = "longitude", precision = 10, scale = 6)
    private Double longitude;

    @TableField("latitude")
    @Column(name = "latitude", precision = 10, scale = 6)
    private Double latitude;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
