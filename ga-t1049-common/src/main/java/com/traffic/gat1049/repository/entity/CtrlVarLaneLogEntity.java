package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.Duration;

/**
 * 可变导向车道控制记录实体类
 * 对应数据库表：ctrl_var_lane_log
 */
@Data
@Accessors(chain = true)
@TableName("ctrl_var_lane_log")
@Entity
@Table(name = "ctrl_var_lane_log")
public class CtrlVarLaneLogEntity {

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
     * 车道序号
     */
    @TableField("lane_no")
    @Column(name = "lane_no", nullable = false)
    private Integer laneNo;

    /**
     * 设置的功能(转向)
     */
    @TableField("movement")
    @Column(name = "movement", length = 2, nullable = false)
    private String movement;

    /**
     * 控制模式
     */
    @TableField("ctrl_mode")
    @Column(name = "ctrl_mode", length = 2, nullable = false)
    private String ctrlMode;

    /**
     * 开始时间
     */
    @TableField("start_time")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 状态
     */
    @TableField("status")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ControlStatus status = ControlStatus.ACTIVE;

    /**
     * 操作员ID
     */
    @TableField("operator_id")
    @Column(name = "operator_id", length = 50)
    private String operatorId;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    /**
     * 控制状态枚举
     */
    public enum ControlStatus {
        ACTIVE,     // 激活
        EXPIRED,    // 过期
        STOPPED     // 停止
    }

    /**
     * 车道转向枚举
     */
    public enum LaneMovement {
        STRAIGHT("01", "直行"),
        LEFT("02", "左转"),
        RIGHT("03", "右转"),
        U_TURN("04", "掉头"),
        STRAIGHT_LEFT("05", "直行+左转"),
        STRAIGHT_RIGHT("06", "直行+右转"),
        LEFT_RIGHT("07", "左转+右转"),
        ALL_DIRECTIONS("08", "全方向");

        private final String code;
        private final String description;

        LaneMovement(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取枚举值
         */
        public static LaneMovement fromCode(String code) {
            for (LaneMovement movement : LaneMovement.values()) {
                if (movement.getCode().equals(code)) {
                    return movement;
                }
            }
            throw new IllegalArgumentException("Unknown lane movement code: " + code);
        }
    }

    /**
     * 控制模式枚举
     */
    public enum VarLaneCtrlMode {
        RESTORE_SIGNAL("00", "恢复信号机控制"),
        FIXED_SIGNAL("11", "信号机控制固定方案"),
        ADAPTIVE_SIGNAL("12", "信号机控制自适应"),
        FIXED_MANUAL("21", "干预控制固定方案"),
        ADAPTIVE_MANUAL("22", "干预控制自适应"),
        OTHER("99", "其他");

        private final String code;
        private final String description;

        VarLaneCtrlMode(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取枚举值
         */
        public static VarLaneCtrlMode fromCode(String code) {
            for (VarLaneCtrlMode mode : VarLaneCtrlMode.values()) {
                if (mode.getCode().equals(code)) {
                    return mode;
                }
            }
            throw new IllegalArgumentException("Unknown control mode code: " + code);
        }
    }

    // ================================================================
    // 业务方法
    // ================================================================

    /**
     * 检查控制是否活跃
     */
    public boolean isActive() {
        return ControlStatus.ACTIVE.equals(this.status);
    }

    /**
     * 检查控制是否过期
     */
    public boolean isExpired() {
        return ControlStatus.EXPIRED.equals(this.status);
    }

    /**
     * 检查控制是否停止
     */
    public boolean isStopped() {
        return ControlStatus.STOPPED.equals(this.status);
    }

    /**
     * 获取车道转向枚举
     */
    public LaneMovement getLaneMovement() {
        return LaneMovement.fromCode(this.movement);
    }

    /**
     * 设置车道转向枚举
     */
    public void setLaneMovement(LaneMovement laneMovement) {
        this.movement = laneMovement.getCode();
    }

    /**
     * 获取控制模式枚举
     */
    public VarLaneCtrlMode getVarLaneCtrlMode() {
        return VarLaneCtrlMode.fromCode(this.ctrlMode);
    }

    /**
     * 设置控制模式枚举
     */
    public void setVarLaneCtrlMode(VarLaneCtrlMode varLaneCtrlMode) {
        this.ctrlMode = varLaneCtrlMode.getCode();
    }

    /**
     * 获取转向描述
     */
    public String getMovementDescription() {
        return getLaneMovement().getDescription();
    }

    /**
     * 获取控制模式描述
     */
    public String getCtrlModeDescription() {
        return getVarLaneCtrlMode().getDescription();
    }

    /**
     * 检查是否为信号机控制
     */
    public boolean isSignalControlled() {
        return ctrlMode.startsWith("1");
    }

    /**
     * 检查是否为手动干预控制
     */
    public boolean isManualControlled() {
        return ctrlMode.startsWith("2");
    }

    /**
     * 检查是否为自适应控制
     */
    public boolean isAdaptiveControlled() {
        return ctrlMode.endsWith("2");
    }

    /**
     * 检查是否为固定方案控制
     */
    public boolean isFixedControlled() {
        return ctrlMode.endsWith("1");
    }

    /**
     * 检查是否为恢复信号机控制
     */
    public boolean isRestoreSignalControlled() {
        return "00".equals(ctrlMode);
    }

    /**
     * 检查是否正在运行中
     */
    public boolean isRunning() {
        if (!isActive()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (startTime.isAfter(now)) {
            return false; // 还未开始
        }

        if (endTime != null && endTime.isBefore(now)) {
            return false; // 已结束
        }

        return true;
    }

    /**
     * 检查是否已过期（但状态未更新）
     */
    public boolean isActuallyExpired() {
        if (endTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(endTime);
    }

    /**
     * 获取控制持续时间（秒）
     */
    public long getControlDurationSeconds() {
        if (startTime == null) {
            return 0;
        }

        LocalDateTime endTimeToUse = endTime != null ? endTime : LocalDateTime.now();
        return Duration.between(startTime, endTimeToUse).getSeconds();
    }

    /**
     * 获取控制持续时间（分钟）
     */
    public long getControlDurationMinutes() {
        return getControlDurationSeconds() / 60;
    }

    /**
     * 获取剩余控制时间（秒）
     */
    public long getRemainingSeconds() {
        if (endTime == null || !isActive()) {
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endTime)) {
            return 0;
        }

        return Duration.between(now, endTime).getSeconds();
    }

    /**
     * 获取剩余控制时间（分钟）
     */
    public long getRemainingMinutes() {
        return getRemainingSeconds() / 60;
    }

    /**
     * 激活控制
     */
    public void activate() {
        this.status = ControlStatus.ACTIVE;
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
    }

    /**
     * 停止控制
     */
    public void stop() {
        this.status = ControlStatus.STOPPED;
        this.endTime = LocalDateTime.now();
    }

    /**
     * 设置控制过期
     */
    public void expire() {
        this.status = ControlStatus.EXPIRED;
        if (this.endTime == null) {
            this.endTime = LocalDateTime.now();
        }
    }

    /**
     * 设置控制时间范围
     */
    public void setControlTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 延长控制时间
     */
    public void extendControlTime(long minutes) {
        if (this.endTime != null) {
            this.endTime = this.endTime.plusMinutes(minutes);
        } else {
            this.endTime = LocalDateTime.now().plusMinutes(minutes);
        }
    }

    /**
     * 缩短控制时间
     */
    public void shortenControlTime(long minutes) {
        if (this.endTime != null) {
            LocalDateTime newEndTime = this.endTime.minusMinutes(minutes);
            LocalDateTime now = LocalDateTime.now();

            // 确保结束时间不会早于当前时间
            if (newEndTime.isBefore(now)) {
                this.endTime = now.plusMinutes(1); // 至少保留1分钟
            } else {
                this.endTime = newEndTime;
            }
        }
    }

    /**
     * 检查是否为永久控制（无结束时间）
     */
    public boolean isPermanentControl() {
        return endTime == null && isActive();
    }

    /**
     * 检查是否为临时控制（有结束时间）
     */
    public boolean isTemporaryControl() {
        return endTime != null;
    }
}
