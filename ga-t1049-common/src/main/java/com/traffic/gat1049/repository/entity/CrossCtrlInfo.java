package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

// 26. 路口控制信息表 (CrossCtrlInfo)
@Data
@Accessors(chain = true)
@TableName("cross_ctrl_info")
@Entity
@Table(name = "cross_ctrl_info")
public class CrossCtrlInfo {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("time")
    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @TableField("control_mode")
    @Column(name = "control_mode", length = 2, nullable = false)
    private String controlMode;

    @TableField("plan_no")
    @Column(name = "plan_no")
    private Integer planNo;

    @TableField("stage_no")
    @Column(name = "stage_no")
    private Integer stageNo;

    @TableField("cycle_len")
    @Column(name = "cycle_len")
    private Integer cycleLen;

    @TableField("split_num")
    @Column(name = "split_num")
    private Integer splitNum;

    @TableField("offset")
    @Column(name = "offset")
    private Integer offset;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
