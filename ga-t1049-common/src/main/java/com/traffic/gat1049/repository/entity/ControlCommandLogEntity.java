package com.traffic.gat1049.repository.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

// 39. 控制命令日志表 (ControlCommandLog)
@Data
@Accessors(chain = true)
@TableName("control_command_log")
@Entity
@Table(name = "control_command_log")
public class ControlCommandLogEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("command_type")
    @Column(name = "command_type", length = 50, nullable = false)
    private String commandType;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14)
    private String crossId;

    @TableField("command_data")
    @Column(name = "command_data", nullable = false, columnDefinition = "JSON")
    private String commandData;

    @TableField("execute_time")
    @Column(name = "execute_time")
    private LocalDateTime executeTime;

    @TableField("result")
    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private ExecuteResult result = ExecuteResult.PENDING;

    @TableField("error_message")
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @TableField("operator_id")
    @Column(name = "operator_id", length = 50)
    private String operatorId;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    public enum ExecuteResult {
        SUCCESS, FAILED, PENDING
    }
}
