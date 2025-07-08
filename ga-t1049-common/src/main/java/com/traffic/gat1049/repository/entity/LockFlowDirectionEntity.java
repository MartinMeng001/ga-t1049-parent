package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 40. 锁定交通流向记录表 (LockFlowDirection)
@Data
@Accessors(chain = true)
@TableName("lock_flow_direction")
@Entity
@Table(name = "lock_flow_direction")
public class LockFlowDirectionEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("type")
    @Column(name = "type", nullable = false)
    private Integer type;

    @TableField("entrance")
    @Column(name = "entrance", length = 1, nullable = false)
    private String entrance;

    @TableField("exit")
    @Column(name = "exit", length = 1, nullable = false)
    private String exit;

    @TableField("lock_type")
    @Column(name = "lock_type", nullable = false)
    private Integer lockType;

    @TableField("lock_stage_no")
    @Column(name = "lock_stage_no")
    private Integer lockStageNo = 0;

    @TableField("duration")
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @TableField("start_time")
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @TableField("status")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LockStatus status = LockStatus.ACTIVE;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    public enum LockStatus {
        ACTIVE, EXPIRED, UNLOCKED
    }
}
