package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 中心预案实体类
 * 对应数据库表：center_plan
 */
@Data
@Accessors(chain = true)
@TableName("center_plan")
@Entity
@Table(name = "center_plan")
public class CenterPlanEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 路口编号
     */
    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    /**
     * 控制方式
     */
    @TableField("control_mode")
    @Column(name = "control_mode", length = 2, nullable = false)
    private String controlMode;

    /**
     * 预案最大运行时长(分钟)
     */
    @TableField("max_run_time")
    @Column(name = "max_run_time", nullable = false)
    private Integer maxRunTime;

    /**
     * 配时方案参数数据 (JSON格式)
     */
    @TableField("plan_data")
    @Column(name = "plan_data", columnDefinition = "JSON", nullable = false)
    private String planData;

    /**
     * 开始执行时间
     */
    @TableField("start_time")
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 预案状态
     */
    @TableField("status")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PlanStatus status = PlanStatus.ACTIVE;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /**
     * 预案状态枚举
     */
    public enum PlanStatus {
        ACTIVE,     // 激活
        EXPIRED,    // 过期
        STOPPED     // 停止
    }

    // ================================================================
    // 业务方法
    // ================================================================

    /**
     * 检查预案是否活跃
     */
    public boolean isActive() {
        return PlanStatus.ACTIVE.equals(this.status);
    }

    /**
     * 检查预案是否过期
     */
    public boolean isExpired() {
        return PlanStatus.EXPIRED.equals(this.status);
    }

    /**
     * 检查预案是否停止
     */
    public boolean isStopped() {
        return PlanStatus.STOPPED.equals(this.status);
    }

    /**
     * 检查预案是否已经超时
     */
    public boolean isOvertime() {
        if (startTime == null || maxRunTime == null) {
            return false;
        }

        LocalDateTime expectedEndTime = startTime.plusMinutes(maxRunTime);
        return LocalDateTime.now().isAfter(expectedEndTime);
    }

    /**
     * 获取预案剩余运行时间（分钟）
     */
    public long getRemainingMinutes() {
        if (startTime == null || maxRunTime == null) {
            return 0;
        }

        LocalDateTime expectedEndTime = startTime.plusMinutes(maxRunTime);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(expectedEndTime)) {
            return 0;
        }

        return java.time.Duration.between(now, expectedEndTime).toMinutes();
    }

    /**
     * 获取预案已运行时间（分钟）
     */
    public long getRunningMinutes() {
        if (startTime == null) {
            return 0;
        }

        LocalDateTime endTimeToUse = endTime != null ? endTime : LocalDateTime.now();
        return java.time.Duration.between(startTime, endTimeToUse).toMinutes();
    }

    /**
     * 激活预案
     */
    public void activate() {
        this.status = PlanStatus.ACTIVE;
        this.startTime = LocalDateTime.now();
        this.endTime = null;
    }

    /**
     * 停止预案
     */
    public void stop() {
        this.status = PlanStatus.STOPPED;
        this.endTime = LocalDateTime.now();
    }

    /**
     * 设置预案过期
     */
    public void expire() {
        this.status = PlanStatus.EXPIRED;
        if (this.endTime == null) {
            this.endTime = LocalDateTime.now();
        }
    }
}
