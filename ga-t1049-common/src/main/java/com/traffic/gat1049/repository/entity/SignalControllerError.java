package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 27. 信号机错误信息表 (SignalControllerError)
@Data
@Accessors(chain = true)
@TableName("signal_controller_error")
@Entity
@Table(name = "signal_controller_error")
public class SignalControllerError {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @TableField("signal_controller_id")
    @Column(name = "signal_controller_id", length = 14, nullable = false)
    private String signalControllerId;

    @TableField("time")
    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @TableField("error_code")
    @Column(name = "error_code", length = 10, nullable = false)
    private String errorCode;

    @TableField("error_description")
    @Column(name = "error_description", columnDefinition = "TEXT")
    private String errorDescription;

    @TableField("severity")
    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private SeverityLevel severity = SeverityLevel.INFO;

    @TableField("is_resolved")
    @Column(name = "is_resolved")
    private Boolean isResolved = false;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    public enum SeverityLevel {
        INFO, WARNING, ERROR, CRITICAL
    }
}
