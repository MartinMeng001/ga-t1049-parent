package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 35. 系统线路关系表 (SysRouteRelation)
@Data
@Accessors(chain = true)
@TableName("sys_route_relation")
@Entity
@Table(name = "sys_route_relation")
public class SysRouteRelation {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("system_id")
    @Column(name = "system_id", length = 20, nullable = false)
    private String systemId;

    @TableField("route_id")
    @Column(name = "route_id", length = 11, nullable = false)
    private String routeId;

    @TableField("is_active")
    @Column(name = "is_active")
    private Boolean isActive = true;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
