package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 11. 信号组参数表 (SignalGroupParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("signal_group_param")
@Entity
@Table(name = "signal_group_param")
public class SignalGroupParamEntity {
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
     * 信号组序号 - SMALLINT UNSIGNED (1-999)
     */
    @TableField("signal_group_no")
    @Column(name = "signal_group_no", nullable = false)
    private Integer signalGroupNo;

    /**
     * 信号组名称 - VARCHAR(50)
     */
    @TableField("name")
    @Column(name = "name", length = 50)
    private String name;

    /**
     * 绿闪时长(秒) - TINYINT UNSIGNED
     */
    @TableField("green_flash_len")
    @Column(name = "green_flash_len")
    private Integer greenFlashLen;

    /**
     * 最大绿灯时长(秒) - TINYINT UNSIGNED
     */
    @TableField("max_green")
    @Column(name = "max_green")
    private Integer maxGreen;

    /**
     * 最小绿灯时长(秒) - TINYINT UNSIGNED
     */
    @TableField("min_green")
    @Column(name = "min_green")
    private Integer minGreen;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    public SignalGroupParamEntity() {
        super();
    }

    public SignalGroupParamEntity(String crossId, Integer signalGroupNo) {
        super();
        this.crossId = crossId;
        this.signalGroupNo = signalGroupNo;
    }
}
