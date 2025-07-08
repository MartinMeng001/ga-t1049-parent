package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 日计划参数表 (DayPlanParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("day_plan_param")
@Entity
@Table(name = "day_plan_param")
public class DayPlanParamEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField("day_plan_no")
    @Column(name = "day_plan_no", nullable = false)
    private Integer dayPlanNo;

    @TableField("day_plan_name")
    @Column(name = "day_plan_name", length = 50)
    private String dayPlanName;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * 构造函数
     */
    public DayPlanParamEntity() {
        super();
    }

    public DayPlanParamEntity(String crossId, Integer dayPlanNo) {
        super();
        this.crossId = crossId;
        this.dayPlanNo = dayPlanNo;
    }

    public DayPlanParamEntity(String crossId, Integer dayPlanNo, String dayPlanName) {
        super();
        this.crossId = crossId;
        this.dayPlanNo = dayPlanNo;
        this.dayPlanName = dayPlanName;
    }

    /**
     * 验证日计划参数的有效性
     * @return 验证结果
     */
    public boolean isValid() {
        if (crossId == null || crossId.trim().isEmpty()) {
            return false;
        }
        if (dayPlanNo == null || dayPlanNo < 1 || dayPlanNo > 999) {
            return false;
        }
        if (dayPlanName != null && dayPlanName.length() > 50) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DayPlanParam{" +
                "id=" + id +
                ", crossId='" + crossId + '\'' +
                ", dayPlanNo=" + dayPlanNo +
                ", dayPlanName='" + dayPlanName + '\'' +
                ", createdTime=" + createdTime +
                ", updatedTime=" + updatedTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayPlanParamEntity that = (DayPlanParamEntity) o;

        if (crossId != null ? !crossId.equals(that.crossId) : that.crossId != null) return false;
        return dayPlanNo != null ? dayPlanNo.equals(that.dayPlanNo) : that.dayPlanNo == null;
    }

    @Override
    public int hashCode() {
        int result = crossId != null ? crossId.hashCode() : 0;
        result = 31 * result + (dayPlanNo != null ? dayPlanNo.hashCode() : 0);
        return result;
    }
}
