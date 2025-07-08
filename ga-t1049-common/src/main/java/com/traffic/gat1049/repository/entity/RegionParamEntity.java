package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
@Data
@Accessors(chain = true)
@TableName("region_param")
@Entity
@Table(name = "region_param")
public class RegionParamEntity {
    @TableId
    @Id
    @Column(name = "region_id", length = 9)
    private String regionId;

    @TableField("region_name")
    @Column(name = "region_name", length = 50, nullable = false)
    private String regionName;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
