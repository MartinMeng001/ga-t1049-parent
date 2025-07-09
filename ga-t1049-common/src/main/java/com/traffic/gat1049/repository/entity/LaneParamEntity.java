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

    /**
     * 路口编号 - CHAR(14)
     */
    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    /**
     * 车道序号 - TINYINT UNSIGNED (1-99)
     */
    @TableField("lane_no")
    @Column(name = "lane_no", nullable = false)
    private Integer laneNo;

    /**
     * 车道所在进口方向 - CHAR(1)
     * E-东, S-南, W-西, N-北
     */
    @TableField("direction")
    @Column(name = "direction", length = 1, nullable = false)
    private String direction;

    /**
     * 车道属性 - TINYINT
     * 0-路口进口；1-路口出口；2-匝道；3-路段车道；9-其他
     */
    @TableField("attribute")
    @Column(name = "attribute", nullable = false)
    private Integer attribute;

    /**
     * 车道转向属性 - CHAR(2)
     * 11-左转；12-直行；13-右转；14-直左；15-左右；16-直右；
     * 21-直左右；22-左直右；23-右直左；24-直左右混行；
     * 31-掉头；32-掉头加左转；33-掉头加直行；34-掉头加右转；99-其他
     */
    @TableField("movement")
    @Column(name = "movement", length = 2, nullable = false)
    private String movement;

    /**
     * 车道特性 - TINYINT
     * 1-机动车；2-非机动车；3-机非混合；4-行人便道；9-其他
     */
    @TableField("feature")
    @Column(name = "feature", nullable = false)
    private Integer feature;

    /**
     * 方位角 - SMALLINT UNSIGNED (0-359度)
     */
    @TableField("azimuth")
    @Column(name = "azimuth")
    private Integer azimuth;

    /**
     * 待行区 - TINYINT(1)
     * 0-无；1-有
     */
    @TableField("waiting_area")
    @Column(name = "waiting_area")
    private Integer waitingArea;

    /**
     * 可变车道功能列表 - JSON
     * 存储可变车道可以支持的转向列表
     */
    @TableField("var_movement_list")
    @Column(name = "var_movement_list", columnDefinition = "JSON")
    private String varMovementList;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * 构造函数
     */
    public LaneParamEntity() {
        super();
    }

    public LaneParamEntity(String crossId, Integer laneNo) {
        super();
        this.crossId = crossId;
        this.laneNo = laneNo;
    }

    public LaneParamEntity(String crossId, Integer laneNo, String direction) {
        super();
        this.crossId = crossId;
        this.laneNo = laneNo;
        this.direction = direction;
    }
}
