package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 人行横道参数表 (PedestrianParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("pedestrian_param")
@Entity
@Table(name = "pedestrian_param")
public class PedestrianParamEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 路口编号 - CHAR(14)
     */
    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    /**
     * 人行横道序号 - TINYINT UNSIGNED (1-99)
     */
    @TableField("pedestrian_no")
    @Column(name = "pedestrian_no", nullable = false)
    private Integer pedestrianNo;

    /**
     * 所在进口方向 - CHAR(1)
     * E-东, S-南, W-西, N-北
     */
    @TableField("direction")
    @Column(name = "direction", length = 1, nullable = false)
    private String direction;

    /**
     * 属性 - TINYINT
     * 1-一次过街；21-二次过街路口进口；22-二次过街路口出口
     */
    @TableField("attribute")
    @Column(name = "attribute", nullable = false)
    private Integer attribute;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    public PedestrianParamEntity() {
        super();
    }

    public PedestrianParamEntity(String crossId, Integer pedestrianNo) {
        super();
        this.crossId = crossId;
        this.pedestrianNo = pedestrianNo;
    }
}
