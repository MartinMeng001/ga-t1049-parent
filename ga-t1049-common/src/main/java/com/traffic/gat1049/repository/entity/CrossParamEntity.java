package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

// ================================
// 6. 路口参数表 (CrossParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("cross_param")
@Entity
@Table(name = "cross_param")
public class CrossParamEntity {
    @TableId
    @Id
    @Column(name = "cross_id", length = 14)
    private String crossId;

    @TableField("cross_name")
    @Column(name = "cross_name", length = 100, nullable = false)
    private String crossName;

    @TableField("longitude")
    @Column(name = "longitude", precision = 10, scale = 6)
    private Double longitude;

    @TableField("latitude")
    @Column(name = "latitude", precision = 10, scale = 6)
    private Double latitude;

    @TableField("description")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
