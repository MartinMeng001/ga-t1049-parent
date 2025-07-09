package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 阶段干预记录实体类
 * 对应数据库表：adjust_stage_log
 */
@Data
@Accessors(chain = true)
@TableName("adjust_stage_log")
@Entity
@Table(name = "adjust_stage_log")
public class AdjustStageLogEntity {

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
     * 干预的阶段号
     */
    @TableField("stage_no")
    @Column(name = "stage_no")
    private Integer stageNo;

    /**
     * 干预类型：1-延长；2-缩短；3-切换到下阶段
     */
    @TableField("type")
    @Column(name = "type", nullable = false)
    private Integer type;

    /**
     * 干预时长(秒)
     */
    @TableField("len")
    @Column(name = "len")
    private Integer len;

    /**
     * 执行时间
     */
    @TableField("execute_time")
    @Column(name = "execute_time")
    private LocalDateTime executeTime;

    /**
     * 执行结果
     */
    @TableField("result")
    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private ExecuteResult result;

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
     * 执行结果枚举
     */
    public enum ExecuteResult {
        SUCCESS,    // 成功
        FAILED      // 失败
    }

    /**
     * 干预类型枚举
     */
    public enum AdjustType {
        EXTEND(1, "延长"),
        SHORTEN(2, "缩短"),
        SKIP_TO_NEXT(3, "切换到下阶段");

        private final int code;
        private final String description;

        AdjustType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取枚举值
         */
        public static AdjustType fromCode(int code) {
            for (AdjustType type : AdjustType.values()) {
                if (type.getCode() == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown adjust type code: " + code);
        }
    }

    // ================================================================
    // 业务方法
    // ================================================================

    /**
     * 检查执行是否成功
     */
    public boolean isSuccess() {
        return ExecuteResult.SUCCESS.equals(this.result);
    }

    /**
     * 检查执行是否失败
     */
    public boolean isFailed() {
        return ExecuteResult.FAILED.equals(this.result);
    }

    /**
     * 获取干预类型枚举
     */
    public AdjustType getAdjustType() {
        return AdjustType.fromCode(this.type);
    }

    /**
     * 设置干预类型枚举
     */
    public void setAdjustType(AdjustType adjustType) {
        this.type = adjustType.getCode();
    }

    /**
     * 获取干预类型描述
     */
    public String getTypeDescription() {
        return getAdjustType().getDescription();
    }

    /**
     * 检查是否为延长操作
     */
    public boolean isExtendOperation() {
        return AdjustType.EXTEND.getCode() == this.type;
    }

    /**
     * 检查是否为缩短操作
     */
    public boolean isShortenOperation() {
        return AdjustType.SHORTEN.getCode() == this.type;
    }

    /**
     * 检查是否为切换操作
     */
    public boolean isSkipOperation() {
        return AdjustType.SKIP_TO_NEXT.getCode() == this.type;
    }

    /**
     * 检查是否需要时长参数
     */
    public boolean needsLengthParameter() {
        return isExtendOperation() || isShortenOperation();
    }

    /**
     * 设置执行成功
     */
    public void setSuccess() {
        this.result = ExecuteResult.SUCCESS;
    }

    /**
     * 设置执行失败
     */
    public void setFailed() {
        this.result = ExecuteResult.FAILED;
    }

    /**
     * 获取执行时长（从执行时间到现在的秒数）
     */
    public long getExecutionElapsedSeconds() {
        if (executeTime == null) {
            return 0;
        }
        return java.time.Duration.between(executeTime, LocalDateTime.now()).getSeconds();
    }

    /**
     * 检查是否为最近的操作（5分钟内）
     */
    public boolean isRecentOperation() {
        if (executeTime == null) {
            return false;
        }
        return executeTime.isAfter(LocalDateTime.now().minusMinutes(5));
    }
}
