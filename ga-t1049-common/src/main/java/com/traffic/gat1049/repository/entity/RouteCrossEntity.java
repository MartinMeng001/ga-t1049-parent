package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 31. 线路路口关系表 (RouteCross)
@Data
@Accessors(chain = true)
@TableName("route_cross")
@Entity
@Table(name = "route_cross")
public class RouteCrossEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("route_id")
    @Column(name = "route_id", length = 11, nullable = false)
    private String routeId;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("sequence")
    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    @TableField("distance")
    @Column(name = "distance", precision = 8, scale = 2)
    private Double distance;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
