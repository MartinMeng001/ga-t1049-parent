package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 调度参数表实体
 *
 * 对应SQL表定义：
 * CREATE TABLE schedule_param (
 *     id INT PRIMARY KEY AUTO_INCREMENT,
 *     cross_id CHAR(14) NOT NULL COMMENT '路口编号',
 *     schedule_no SMALLINT UNSIGNED NOT NULL COMMENT '调度号(1-999)',
 *     schedule_name VARCHAR(50) COMMENT '调度名称',
 *     type TINYINT NOT NULL COMMENT '调度类型：1-特殊日调度；2-时间段周调度；3-周调度',
 *     start_day CHAR(5) NOT NULL COMMENT '开始月日(MM-DD)',
 *     end_day CHAR(5) NOT NULL COMMENT '结束月日(MM-DD)',
 *     week_day TINYINT COMMENT '周几(1-7分别代表周一至周日)',
 *     day_plan_no SMALLINT UNSIGNED NOT NULL COMMENT '日计划号',
 *     created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 *     updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 *     FOREIGN KEY (cross_id) REFERENCES cross_param(cross_id) ON DELETE CASCADE,
 *     UNIQUE KEY uk_cross_schedule (cross_id, schedule_no)
 * ) COMMENT = '调度参数表';
 */
@Data
@Accessors(chain = true)
@TableName("schedule_param")
@Entity
@Table(name = "schedule_param")
public class ScheduleParamEntity {

    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("schedule_no")
    @Column(name = "schedule_no", nullable = false)
    private Integer scheduleNo;

    @TableField("schedule_name")
    @Column(name = "schedule_name", length = 50)
    private String scheduleName;

    @TableField("type")
    @Column(name = "type", nullable = false)
    private Integer type;

    @TableField("start_day")
    @Column(name = "start_day", length = 5, nullable = false)
    private String startDay;

    @TableField("end_day")
    @Column(name = "end_day", length = 5, nullable = false)
    private String endDay;

    @TableField("week_day")
    @Column(name = "week_day")
    private Integer weekDay;

    @TableField("day_plan_no")
    @Column(name = "day_plan_no", nullable = false)
    private Integer dayPlanNo;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * 构造函数
     */
    public ScheduleParamEntity() {
        super();
    }

    public ScheduleParamEntity(String crossId, Integer scheduleNo) {
        super();
        this.crossId = crossId;
        this.scheduleNo = scheduleNo;
    }

    public ScheduleParamEntity(String crossId, Integer scheduleNo, String scheduleName,
                               Integer type, String startDay, String endDay, Integer dayPlanNo) {
        super();
        this.crossId = crossId;
        this.scheduleNo = scheduleNo;
        this.scheduleName = scheduleName;
        this.type = type;
        this.startDay = startDay;
        this.endDay = endDay;
        this.dayPlanNo = dayPlanNo;
    }

    /**
     * 验证调度参数的有效性
     * @return 验证结果
     */
    public boolean isValid() {
        if (crossId == null || crossId.trim().isEmpty()) {
            return false;
        }
        if (scheduleNo == null || scheduleNo < 1 || scheduleNo > 999) {
            return false;
        }
        if (scheduleName != null && scheduleName.length() > 50) {
            return false;
        }
        if (type == null || type < 1 || type > 3) {
            return false;
        }
        if (startDay == null || startDay.trim().isEmpty() || !isValidMonthDay(startDay)) {
            return false;
        }
        if (endDay == null || endDay.trim().isEmpty() || !isValidMonthDay(endDay)) {
            return false;
        }
        if (weekDay != null && (weekDay < 1 || weekDay > 7)) {
            return false;
        }
        if (dayPlanNo == null || dayPlanNo < 1 || dayPlanNo > 999) {
            return false;
        }
        return true;
    }

    /**
     * 验证月日格式是否正确 (MM-DD)
     */
    private boolean isValidMonthDay(String monthDay) {
        if (monthDay == null || monthDay.length() != 5 || !monthDay.contains("-")) {
            return false;
        }

        String[] parts = monthDay.split("-");
        if (parts.length != 2) {
            return false;
        }

        try {
            int month = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            return month >= 1 && month <= 12 && day >= 1 && day <= 31;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取调度类型描述
     */
    public String getTypeDescription() {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "特殊日调度";
            case 2: return "时间段周调度";
            case 3: return "周调度";
            default: return "未知";
        }
    }

    /**
     * 获取周几描述
     */
    public String getWeekDayDescription() {
        if (weekDay == null) return "不限";
        switch (weekDay) {
            case 1: return "周一";
            case 2: return "周二";
            case 3: return "周三";
            case 4: return "周四";
            case 5: return "周五";
            case 6: return "周六";
            case 7: return "周日";
            default: return "未知";
        }
    }

    @Override
    public String toString() {
        return "ScheduleParam{" +
                "id=" + id +
                ", crossId='" + crossId + '\'' +
                ", scheduleNo=" + scheduleNo +
                ", scheduleName='" + scheduleName + '\'' +
                ", type=" + type + "(" + getTypeDescription() + ")" +
                ", startDay='" + startDay + '\'' +
                ", endDay='" + endDay + '\'' +
                ", weekDay=" + weekDay + "(" + getWeekDayDescription() + ")" +
                ", dayPlanNo=" + dayPlanNo +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleParamEntity that = (ScheduleParamEntity) o;

        if (crossId != null ? !crossId.equals(that.crossId) : that.crossId != null) return false;
        return scheduleNo != null ? scheduleNo.equals(that.scheduleNo) : that.scheduleNo == null;
    }

    @Override
    public int hashCode() {
        int result = crossId != null ? crossId.hashCode() : 0;
        result = 31 * result + (scheduleNo != null ? scheduleNo.hashCode() : 0);
        return result;
    }
}