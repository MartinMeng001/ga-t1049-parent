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
    /**
     * 路口编号：机构代码前6位+80+4位路口代码
     * 例如：11010180001
     */
    @TableId
    @Id
    @Column(name = "cross_id", length = 14)
    private String crossId;

    /**
     * 路口名称
     */
    @TableField("cross_name")
    @Column(name = "cross_name", length = 50, nullable = false)
    private String crossName;

    /**
     * 路口形状特征
     * 10-行人过街；12-2次行人过街；23-T形Y形；24-十字形；
     * 35-五岔；36-六岔；39-多岔；40-环形；50-匝道；
     * 51-匝道入口；52-匝道出口；61-快速路主路；90-其他
     */
    @TableField("feature")
    @Column(name = "feature", nullable = false)
    private Integer feature;

    /**
     * 路口等级
     * 11-一级；12-二级；13-三级；21-四级；22-五级；31-六级；99-其他
     */
    @TableField("grade")
    @Column(name = "grade", length = 2, nullable = false)
    private String grade;

    /**
     * 绿冲突矩阵
     */
    @TableField("green_conflict_matrix")
    @Column(name = "green_conflict_matrix", columnDefinition = "TEXT")
    private String greenConflictMatrix;

    /**
     * 路口中心位置经度
     */
    @TableField("longitude")
    @Column(name = "longitude")
    private Double longitude;

    /**
     * 路口中心位置纬度
     */
    @TableField("latitude")
    @Column(name = "latitude")
    private Double latitude;

    /**
     * 路口位置海拔高度(米)
     */
    @TableField("altitude")
    @Column(name = "altitude")
    private Integer altitude;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
