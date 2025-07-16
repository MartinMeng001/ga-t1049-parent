package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 12. 信号灯组参数表 (LampGroupParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("lamp_group_param")
@Entity
@Table(name = "lamp_group_param")
public class LampGroupParamEntity {

    /**
     * 主键ID - AUTO_INCREMENT
     */
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 路口编号 - CHAR(14) NOT NULL
     */
    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    /**
     * 信号灯组序号 - TINYINT UNSIGNED NOT NULL (1-99)
     */
    @TableField("lamp_group_no")
    @Column(name = "lamp_group_no", nullable = false)
    private Integer lampGroupNo;

    /**
     * 控制进口方向 - CHAR(1) NOT NULL
     */
    @TableField("direction")
    @Column(name = "direction", length = 1, nullable = false)
    private String direction;

    /**
     * 灯组类型 - CHAR(2) NOT NULL
     */
    @TableField("type")
    @Column(name = "type", length = 2, nullable = false)
    private String type;

    /**
     * 是否有效 - TINYINT(1) NOT NULL '是否有效：1-有效；0-无效'
     */
    @TableField("valid")
    @Column(name = "valid", length = 1, nullable = false)
    private Integer valid;

    /**
     * 创建时间 - TIMESTAMP DEFAULT CURRENT_TIMESTAMP
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间 - TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * 默认构造函数
     */
    public LampGroupParamEntity() {
        super();
    }

    /**
     * 带参数的构造函数
     */
    public LampGroupParamEntity(String crossId, Integer lampGroupNo, String direction, String type, Integer valid) {
        super();
        this.crossId = crossId;
        this.lampGroupNo = lampGroupNo;
        this.direction = direction;
        this.type = type;
        this.valid = valid;
    }
}
