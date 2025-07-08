package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 3. 线路参数表 (RouteParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("route_param")
@Entity
@Table(name = "route_param")
public class RouteParamEntity {
    @TableId
    @Id
    @Column(name = "route_id", length = 11)
    private String routeId;

    @TableField("route_name")
    @Column(name = "route_name", length = 50, nullable = false)
    private String routeName;

    @TableField("type")
    @Column(name = "type", nullable = false)
    private Integer type;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
