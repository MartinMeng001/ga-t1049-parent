package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// 30. 子区路口关系表 (SubRegionCross)
@Data
@Accessors(chain = true)
@TableName("sub_region_cross")
@Entity
@Table(name = "sub_region_cross")
public class SubRegionCross {
    @TableId(type = IdType.AUTO)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TableField("sub_region_id")
    @Column(name = "sub_region_id", length = 11, nullable = false)
    private String subRegionId;

    @TableField("cross_id")
    @Column(name = "cross_id", length = 14, nullable = false)
    private String crossId;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;
}
