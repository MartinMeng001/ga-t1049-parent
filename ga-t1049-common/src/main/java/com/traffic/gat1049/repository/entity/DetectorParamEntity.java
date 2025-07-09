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

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 路口编号
     */
    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    /**
     * 检测器序号(1-999)
     * 注意：数据库中是detector_no，不是detector_id
     */
    @TableField("detector_no")
    @Column(name = "detector_no", nullable = false)
    private Integer detectorNo;

    /**
     * 检测器类型
     * 1-线圈；2-视频；3-地磁；4-微波；5-RFID；6-雷视一体；9-其他
     * 注意：数据库中是type，不是detector_type
     */
    @TableField("type")
    @Column(name = "type", nullable = false)
    private Integer type;

    /**
     * 检测位置
     * 1-进口；2-出口；9-其他
     * 注意：数据库中是position，不是direction_no
     */
    @TableField("position")
    @Column(name = "position", nullable = false)
    private Integer position;

    /**
     * 检测对象
     * 从左到右分别标记机动车、非机动车、行人(1-支持，0-不支持)
     */
    @TableField("target")
    @Column(name = "target", length = 3, nullable = false)
    private String target;

    /**
     * 距停车线距离(厘米)
     * 注意：数据库中存储的是厘米，类型是INT
     */
    @TableField("distance")
    @Column(name = "distance", nullable = false)
    private Integer distance;

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

    // ================================================================
    // 注意：以下字段在数据库表中不存在，需要通过关联表获取
    // ================================================================

    // detector_name - 不在主表中，可以动态生成
    // lane_no - 通过detector_lane关联表获取
    // longitude、latitude - 不在detector_param表中
    // direction_no - 不在detector_param表中，应该使用position字段
}
