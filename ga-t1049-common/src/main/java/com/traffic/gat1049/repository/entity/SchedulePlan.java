package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 19. 时段计划表 (SchedulePlan)
// ================================
@Data
@Accessors(chain = true)
@TableName("schedule_plan")
@Entity
@Table(name = "schedule_plan")
public class SchedulePlan {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("schedule_id")
    @Column(name = "schedule_id", nullable = false)
    private Integer scheduleId;

    @TableField("start_time")
    @Column(name = "start_time", length = 5, nullable = false)
    private String startTime;

    @TableField("plan_no")
    @Column(name = "plan_no", nullable = false)
    private Integer planNo;

    @TableField("control_mode")
    @Column(name = "control_mode", length = 2, nullable = false)
    private String controlMode;

    @TableField("week_day")
    @Column(name = "week_day", nullable = false)
    private Integer weekDay;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
