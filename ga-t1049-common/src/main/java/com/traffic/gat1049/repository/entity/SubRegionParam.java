package com.traffic.gat1049.repository.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
// ================================
// 4. 子区参数表 (SubRegionParam)
// ================================
@Data
@Accessors(chain = true)
@TableName("sub_region_param")
@Entity
@Table(name = "sub_region_param")
public class SubRegionParam {
    @TableId
    @Id
    @Column(name = "sub_region_id", length = 11)
    private String subRegionId;

    @TableField("sub_region_name")
    @Column(name = "sub_region_name", length = 50, nullable = false)
    private String subRegionName;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
}
