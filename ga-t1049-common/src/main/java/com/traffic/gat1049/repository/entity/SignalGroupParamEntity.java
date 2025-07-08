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

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("signal_group_no")
    @Column(name = "signal_group_no", nullable = false)
    private Integer signalGroupNo;

    @TableField("signal_group_name")
    @Column(name = "signal_group_name", length = 30)
    private String signalGroupName;

    @TableField("signal_group_type")
    @Column(name = "signal_group_type")
    private Integer signalGroupType = 0;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
