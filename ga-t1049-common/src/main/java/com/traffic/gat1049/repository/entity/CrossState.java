package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 8. 路口状态表 (CrossState)
// ================================
@Data
@Accessors(chain = true)
@TableName("cross_state")
@Entity
@Table(name = "cross_state")
public class CrossState {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

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
