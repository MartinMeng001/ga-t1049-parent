package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 7. 系统状态表 (SysState)
// ================================
@Data
@Accessors(chain = true)
@TableName("sys_state")
@Entity
@Table(name = "sys_state")
public class SysStateEntity {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("system_id")
    @Column(name = "system_id", length = 20, nullable = false)
    private String systemId;

    @TableField("value")
    @Enumerated(EnumType.STRING)
    @Column(name = "value", nullable = false)
    private StateValue value;

    @TableField("time")
    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    public enum StateValue {
        Online, Offline, Error
    }
}
