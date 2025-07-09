package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 14. 信号组灯组关联表 (SignalGroupLampGroup)
// ================================
@Data
@Accessors(chain = true)
@TableName("signal_group_lamp_group")
@Entity
@Table(name = "signal_group_lamp_group")
public class SignalGroupLampGroupEntity {

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
     * 信号组序号 - SMALLINT UNSIGNED
     */
    @TableField("signal_group_no")
    @Column(name = "signal_group_no", nullable = false)
    private Integer signalGroupNo;

    /**
     * 信号灯组序号 - TINYINT UNSIGNED
     */
    @TableField("lamp_group_no")
    @Column(name = "lamp_group_no", nullable = false)
    private Integer lampGroupNo;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    public SignalGroupLampGroupEntity() {
        super();
    }

    public SignalGroupLampGroupEntity(String crossId, Integer signalGroupNo, Integer lampGroupNo) {
        super();
        this.crossId = crossId;
        this.signalGroupNo = signalGroupNo;
        this.lampGroupNo = lampGroupNo;
    }
}
