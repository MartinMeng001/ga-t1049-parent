package com.traffic.gat1049.repository.entity;

// ================================
// 运行信息数据表
// ================================

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

// 21. 路口周期表 (CrossCycle)
@Data
@Accessors(chain = true)
@TableName("cross_cycle")
@Entity
@Table(name = "cross_cycle")
public class CrossCycleEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("start_time")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @TableField("cycle_len")
    @Column(name = "cycle_len", nullable = false)
    private Integer cycleLen;

    @TableField("plan_no")
    @Column(name = "plan_no")
    private Integer planNo;

    @TableField("split_num")
    @Column(name = "split_num")
    private Integer splitNum;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
