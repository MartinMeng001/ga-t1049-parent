package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 5. 信号机参数表 (SignalControllerParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("signal_controller_param")
@Entity
@Table(name = "signal_controller_param")
public class SignalControllerParam {
    @TableId
    @Id
    @Column(name = "signal_controller_id", length = 14)
    private String signalControllerId;

    @TableField("signal_controller_name")
    @Column(name = "signal_controller_name", length = 50)
    private String signalControllerName;

    @TableField("manufacturer")
    @Column(name = "manufacturer", length = 50)
    private String manufacturer;

    @TableField("model")
    @Column(name = "model", length = 30)
    private String model;

    @TableField("version")
    @Column(name = "version", length = 20)
    private String version;

    @TableField("description")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
